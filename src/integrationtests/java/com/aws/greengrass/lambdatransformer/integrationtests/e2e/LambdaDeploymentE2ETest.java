/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.integrationtests.e2e;

import com.aws.greengrass.componentmanager.ComponentStore;
import com.aws.greengrass.componentmanager.models.ComponentIdentifier;
import com.aws.greengrass.deployment.DeploymentQueue;
import com.aws.greengrass.deployment.DeploymentService;
import com.aws.greengrass.deployment.DeploymentStatusKeeper;
import com.aws.greengrass.deployment.DeviceConfiguration;
import com.aws.greengrass.deployment.model.ConfigurationUpdateOperation;
import com.aws.greengrass.deployment.model.Deployment;
import com.aws.greengrass.deployment.model.LocalOverrideRequest;
import com.aws.greengrass.integrationtests.e2e.util.IotJobsUtils;
import com.aws.greengrass.logging.impl.GreengrassLogMessage;
import com.aws.greengrass.testcommons.testutilities.GGExtension;
import com.aws.greengrass.testcommons.testutilities.TestUtils;
import com.aws.greengrass.util.Utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.semver4j.Semver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.services.greengrassv2.model.ComponentDeploymentSpecification;
import software.amazon.awssdk.services.greengrassv2.model.CreateDeploymentRequest;
import software.amazon.awssdk.services.greengrassv2.model.CreateDeploymentResponse;
import software.amazon.awssdk.services.greengrassv2data.model.GreengrassV2DataException;
import software.amazon.awssdk.services.iot.model.JobExecutionStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.aws.greengrass.componentmanager.KernelConfigResolver.VERSION_CONFIG_KEY;
import static com.aws.greengrass.config.Topic.DEFAULT_VALUE_TIMESTAMP;
import static com.aws.greengrass.deployment.DeploymentStatusKeeper.DEPLOYMENT_ID_KEY_NAME;
import static com.aws.greengrass.deployment.DeploymentStatusKeeper.DEPLOYMENT_STATUS_KEY_NAME;
import static com.aws.greengrass.deployment.DeviceConfiguration.DEFAULT_NUCLEUS_COMPONENT_NAME;
import static com.aws.greengrass.deployment.DeviceConfiguration.GGC_VERSION_ENV;
import static com.aws.greengrass.lifecyclemanager.GreengrassService.SERVICES_NAMESPACE_TOPIC;
import static com.aws.greengrass.lifecyclemanager.GreengrassService.SETENV_CONFIG_NAMESPACE;
import static com.aws.greengrass.testcommons.testutilities.ExceptionLogProtector.ignoreExceptionOfType;
import static com.aws.greengrass.testcommons.testutilities.ExceptionLogProtector.ignoreExceptionUltimateCauseWithMessageSubstring;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(GGExtension.class)
@Tag("E2E")
@EnabledOnOs(OS.LINUX)
public class LambdaDeploymentE2ETest extends BaseE2ETestCase {
    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    private static final String NUCLEUS_VERSION = "2.4.99";
    private static final String NUCLEUS_NAME = "aws.greengrass.Nucleus";

    CountDownLatch stdoutCountdown;
    private Path localStoreContentPath;
    private DeploymentQueue deploymentQueue;

    protected LambdaDeploymentE2ETest() throws Exception {
        super();
    }

    @AfterEach
    void afterEach() {
        if (kernel != null) {
            kernel.shutdown();
        }
        // Cleanup all IoT thing resources we created
        cleanup();
    }

    @BeforeEach
    void launchKernel(ExtensionContext context) throws Exception {
        ignoreExceptionOfType(context, GreengrassV2DataException.class);
        ignoreExceptionUltimateCauseWithMessageSubstring(context, "Unable to locate the unpack directory of Nucleus artifacts");

        initKernel();

        kernel.getConfig().lookup(SERVICES_NAMESPACE_TOPIC, DEFAULT_NUCLEUS_COMPONENT_NAME,
                VERSION_CONFIG_KEY).withNewerValue(DEFAULT_VALUE_TIMESTAMP + 1, NUCLEUS_VERSION);
        kernel.getConfig().lookup(SETENV_CONFIG_NAMESPACE, GGC_VERSION_ENV).dflt(NUCLEUS_VERSION);


        // Copy recipe to component store
        ComponentStore componentStore = kernel.getContext().get(ComponentStore.class);
        ComponentIdentifier componentIdentifier = new ComponentIdentifier(NUCLEUS_NAME, new Semver(NUCLEUS_VERSION));
        Path destinationRecipePath = componentStore.resolveRecipePath(componentIdentifier);
        if (!Files.exists(destinationRecipePath)) {
            DeploymentService.copyRecipeFileToComponentStore(componentStore,
                    e2eTestPkgStoreDir.resolve("recipes/aws.greengrass.Nucleus-2.4.99.yaml"), logger);
        }

        kernel.launch();

        // GG_NEEDS_REVIEW: TODO: Without this sleep, DeploymentService sometimes is not able to pick up new IoT job created here,
        // causing these tests to fail. There may be a race condition between DeploymentService startup logic and
        // creating new IoT job here.
        Thread.sleep(10_000);
        setDeviceConfig(kernel, DeviceConfiguration.DEPLOYMENT_POLLING_FREQUENCY_SECONDS, 1L);

        deploymentQueue =  kernel.getContext().get(DeploymentQueue.class);

        localStoreContentPath =
                Paths.get(LambdaDeploymentE2ETest.class.getResource("component_resources").toURI());
        renameTestTransformerJarsToTransformerJars(localStoreContentPath.resolve("artifacts"));
    }

    @Timeout(value = 10, unit = TimeUnit.MINUTES)
    @Test
    void e2e()
            throws Exception {
        CountDownLatch firstDeploymentCDL = new CountDownLatch(1);
        DeploymentStatusKeeper deploymentStatusKeeper = kernel.getContext().get(DeploymentStatusKeeper.class);
        deploymentStatusKeeper.registerDeploymentStatusConsumer(Deployment.DeploymentType.LOCAL, (status) -> {
            if(status.get(DEPLOYMENT_ID_KEY_NAME).equals("lambdaDeployment") &&
                    status.get(DEPLOYMENT_STATUS_KEY_NAME).equals("SUCCEEDED")){
                firstDeploymentCDL.countDown();
            }
            return true;
        },"LambdaTemplateTest");

        // Set up stdout listener to capture stdout for verify interpolation
        List<String> stdouts = new CopyOnWriteArrayList<>();
        Consumer<GreengrassLogMessage> listener = m -> {
            String messageOnStdout = m.getMessage();
            if (messageOnStdout != null && messageOnStdout.contains("CustomerApp output.")) {
                stdouts.add(messageOnStdout);
                stdoutCountdown.countDown(); // countdown when received output to verify
            }
        };

        try (AutoCloseable l = TestUtils.createCloseableLogListener(listener)) {
            stdoutCountdown = new CountDownLatch(1);
            // 1st Deployment to have some services running in Kernel with default configuration
            CreateDeploymentRequest createDeployment1 = CreateDeploymentRequest.builder().components(
                    Utils.immutableMap(NUCLEUS_NAME,
                            ComponentDeploymentSpecification.builder().componentVersion(NUCLEUS_VERSION).build(),
                            "FakeLambda",
                            ComponentDeploymentSpecification.builder().componentVersion("1.0.0").build()))
                    .build();

            CreateDeploymentResponse createDeploymentResult1 = draftAndCreateDeployment(createDeployment1);

            IotJobsUtils.waitForJobExecutionStatusToSatisfy(iotClient, createDeploymentResult1.iotJobId(),
                    thingInfo.getThingName(), Duration.ofMinutes(2), s -> s.equals(JobExecutionStatus.SUCCEEDED));
        }

        String recipeDir = localStoreContentPath.resolve("recipes").toAbsolutePath().toString();
        String artifactsDir = localStoreContentPath.resolve("artifacts").toAbsolutePath().toString();

        Map<String, String> componentsToMerge = new HashMap<>();
        componentsToMerge.put(NUCLEUS_NAME, NUCLEUS_VERSION);
        componentsToMerge.put("LambdaA", "1.0.0");

        Map<String, ConfigurationUpdateOperation> updateConfig = new HashMap<>();

        LocalOverrideRequest request = LocalOverrideRequest.builder().requestId("lambdaDeployment")
                .componentsToMerge(componentsToMerge)
                .requestTimestamp(System.currentTimeMillis())
                .configurationUpdate(updateConfig)
                .recipeDirectoryPath(recipeDir).artifactsDirectoryPath(artifactsDir).build();

        submitLocalDocument(request);

        assertTrue(firstDeploymentCDL.await(10, TimeUnit.SECONDS), "Templating deployment did not succeed");
    }

    private void submitLocalDocument(LocalOverrideRequest request) throws Exception {
        Deployment deployment = new Deployment(OBJECT_MAPPER.writeValueAsString(request), Deployment.DeploymentType.LOCAL, request.getRequestId());
        deploymentQueue.offer(deployment);
    }

    private void renameTestTransformerJarsToTransformerJars(Path artifactsDir) throws IOException {
        try (Stream<Path> files = Files.walk(artifactsDir)) {
            for (Path r : files.collect(Collectors.toList())) {
                if (!r.toFile().isDirectory() && "transformer-packed.jar".equals(r.getFileName().toString())) {
                    Files.move(r, r.resolveSibling("transformer.jar"), REPLACE_EXISTING);
                }
            }
        }
    }
}
