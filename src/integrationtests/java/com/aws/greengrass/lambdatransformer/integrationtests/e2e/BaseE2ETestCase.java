/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.integrationtests.e2e;

import com.aws.greengrass.componentmanager.models.ComponentIdentifier;
import com.aws.greengrass.deployment.DeviceConfiguration;
import com.aws.greengrass.deployment.exceptions.DeviceConfigurationException;
import com.aws.greengrass.easysetup.DeviceProvisioningHelper;
import com.aws.greengrass.integrationtests.e2e.helper.ComponentServiceTestHelper;
import com.aws.greengrass.integrationtests.e2e.util.IotJobsUtils;
import com.aws.greengrass.lifecyclemanager.Kernel;
import com.aws.greengrass.logging.api.Logger;
import com.aws.greengrass.logging.impl.LogManager;
import com.aws.greengrass.tes.CredentialRequestHandler;
import com.aws.greengrass.tes.TokenExchangeService;
import com.aws.greengrass.testcommons.testutilities.GGExtension;
import com.aws.greengrass.util.IamSdkClientFactory;
import com.aws.greengrass.util.IotSdkClientFactory;
import com.aws.greengrass.util.RegionUtils;
import com.aws.greengrass.util.Utils;
import com.aws.greengrass.util.platforms.Platform;
import com.aws.greengrass.util.platforms.unix.DarwinPlatform;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.io.TempDir;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.greengrassv2.GreengrassV2Client;
import software.amazon.awssdk.services.greengrassv2.model.*;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateThingGroupResponse;
import software.amazon.awssdk.services.iot.model.DeleteConflictException;
import software.amazon.awssdk.services.iot.model.InvalidRequestException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.aws.greengrass.componentmanager.KernelConfigResolver.CONFIGURATION_CONFIG_KEY;
import static com.aws.greengrass.lifecyclemanager.GreengrassService.SERVICES_NAMESPACE_TOPIC;
import static com.aws.greengrass.testcommons.testutilities.ExceptionLogProtector.ignoreExceptionUltimateCauseWithMessageSubstring;
import static software.amazon.awssdk.services.greengrassv2.model.DeploymentComponentUpdatePolicyAction.NOTIFY_COMPONENTS;
import static software.amazon.awssdk.services.greengrassv2.model.DeploymentFailureHandlingPolicy.DO_NOTHING;

/**
 * Base class for E2E tests, with the following functionality: * Bootstrap one IoT thing group and one IoT thing, and
 * add thing to the group. * Manages integration points and API calls to Greengrass cloud services in Beta stage.
 */
@ExtendWith(GGExtension.class)
public class BaseE2ETestCase implements AutoCloseable {
    protected static final Region GAMMA_REGION = Region.US_EAST_1;
    private static final String TES_ROLE_NAME = "E2ETestsTesRole";
    protected static final String TES_ROLE_ALIAS_NAME = "E2ETestsTesRoleAlias";
    private static final String TES_ROLE_POLICY_NAME = "E2ETestsTesRolePolicy";
    private static final String TES_ROLE_POLICY_DOCUMENT = "{\n"
            + "    \"Version\": \"2012-10-17\",\n"
            + "    \"Statement\": [\n"
            + "        {\n"
            + "            \"Effect\": \"Allow\",\n"
            + "            \"Action\": [\n"
            + "                \"greengrass:*\",\n"
            + "                \"s3:Get*\",\n"
            + "                \"s3:List*\"\n"
            + "            ],\n"
            + "            \"Resource\": \"*\"\n"
            + "        }\n"
            + "    ]\n"
            + "}";
    protected static final String TEST_COMPONENT_ARTIFACTS_S3_BUCKET_PREFIX = "eg-e2e-test-artifacts";
    protected static final String TEST_COMPONENT_ARTIFACTS_S3_BUCKET =
            TEST_COMPONENT_ARTIFACTS_S3_BUCKET_PREFIX + UUID.randomUUID().toString();

    protected static final Logger logger = LogManager.getLogger(BaseE2ETestCase.class);

    protected static final String testComponentSuffix = "_" + UUID.randomUUID().toString();
    protected static Optional<String> tesRolePolicyArn;
    protected static final IotSdkClientFactory.EnvironmentStage envStage = IotSdkClientFactory.EnvironmentStage.PROD;

    protected final Set<CancelDeploymentRequest> createdDeployments = new HashSet<>();
    protected final Set<String> createdThingGroups = new HashSet<>();
    protected DeviceProvisioningHelper.ThingInfo thingInfo;
    protected String thingGroupName;
    protected String thingGroupArn;
    protected CreateThingGroupResponse thingGroupResp;

    protected DeviceProvisioningHelper deviceProvisioningHelper =
            new DeviceProvisioningHelper(GAMMA_REGION.toString(), envStage.toString(), System.out);

    @TempDir
    protected Path tempRootDir;

    @TempDir
    protected static Path e2eTestPkgStoreDir;

    protected Kernel kernel;

    protected static IotClient iotClient;

    static {
        try {
            iotClient = IotSdkClientFactory.getIotClient(GAMMA_REGION.toString(), envStage,
                    new HashSet<>(Arrays.asList(InvalidRequestException.class, DeleteConflictException.class)));
        } catch (URISyntaxException e) {
            logger.atError().setCause(e).log("Caught exception while initializing Iot client");
            throw new RuntimeException(e);
        }
    }

    protected static final GreengrassV2Client greengrassClient = GreengrassV2Client.builder()
            .endpointOverride(URI.create(RegionUtils.getGreengrassControlPlaneEndpoint(GAMMA_REGION.toString(),
                    envStage)))
            .region(GAMMA_REGION).build();
    protected static final IamClient iamClient = IamSdkClientFactory.getIamClient(GAMMA_REGION.toString());
    protected static final S3Client s3Client = S3Client.builder().region(GAMMA_REGION).build();

    private static final ComponentIdentifier[] componentsWithArtifactsInS3 = {};

    private static final Map<ComponentIdentifier, String> componentArns = new HashMap<>();

    @BeforeEach
    void beforeEach(ExtensionContext context) {
        // MQTT connection may close quickly in some tests, this is OK and should not be a concern.
        ignoreExceptionUltimateCauseWithMessageSubstring(context, "The connection was closed unexpectedly");
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        initializePackageStore();

        // Self hosted artifacts must exist in S3 before creating a component version
//        createS3BucketsForTestComponentArtifacts();
//        uploadComponentArtifactToS3(componentsWithArtifactsInS3);
//        uploadTestComponentsToCms(componentsWithArtifactsInS3);
    }

    @AfterAll
    static void afterAll() {
        try {
            List<ComponentIdentifier> allComponents = new ArrayList<>(Arrays.asList(componentsWithArtifactsInS3));
            for (ComponentIdentifier component : allComponents) {
                String componentArn = componentArns.get(component);
                if (Utils.isEmpty(componentArn)) {
                    logger.atWarn().kv("component-name", component.getName())
                            .kv("component-version", component.getVersion())
                            .log("No component arn found to make delete request for cleanup");
                } else {
                    ComponentServiceTestHelper.deleteComponent(greengrassClient, componentArn);
                }
            }
        } finally {
            cleanUpTestComponentArtifactsFromS3();
        }
    }

    protected BaseE2ETestCase() throws Exception {
        thingInfo = deviceProvisioningHelper.createThingForE2ETests();
        thingGroupResp = IotJobsUtils.createThingGroupAndAddThing(iotClient, thingInfo);
        thingGroupName = thingGroupResp.thingGroupName();
        thingGroupArn = thingGroupResp.thingGroupArn();
        createdThingGroups.add(thingGroupName);
    }

    public static void setDefaultRunWithUser(Kernel kernel) {
        String user = "nobody";
        if (Platform.getInstance() instanceof DarwinPlatform) {
            user = System.getProperty("user.name");
        }
        new DeviceConfiguration(kernel).getRunWithDefaultPosixUser().dflt(user);
    }

    protected void initKernel()
            throws IOException, DeviceConfigurationException, InterruptedException {
        kernel = new Kernel()
                .parseArgs("-r", tempRootDir.toAbsolutePath().toString(), "-ar", GAMMA_REGION.toString(), "-es",
                        envStage.toString());
        setupTesRoleAndAlias();
        setDefaultRunWithUser(kernel);
        deviceProvisioningHelper.updateKernelConfigWithIotConfiguration(kernel, thingInfo, GAMMA_REGION.toString(),
                TES_ROLE_ALIAS_NAME);
        // Force context to create TES now to that it subscribes to the role alias changes
        kernel.getContext().get(TokenExchangeService.class);
        while (kernel.getContext().get(CredentialRequestHandler.class).getAwsCredentialsBypassCache() == null) {
            logger.atInfo().kv("roleAlias", TES_ROLE_ALIAS_NAME)
                    .log("Waiting 5 seconds for TES to get credentials that work");
            Thread.sleep(5_000);
        }
    }

    private static void initializePackageStore() throws Exception {
        Path localStoreContentPath = Paths
                .get(BaseE2ETestCase.class.getResource("component_resources").getPath());

        // copy to tmp directory
        FileUtils.copyDirectory(localStoreContentPath.toFile(), e2eTestPkgStoreDir.toFile());
    }

    private static void cleanUpTestComponentArtifactsFromS3() {
        try {
            ListObjectsResponse objectsInArtifactsBucket = s3Client.listObjects(
                    ListObjectsRequest.builder().bucket(TEST_COMPONENT_ARTIFACTS_S3_BUCKET).build());
            for (S3Object artifact : objectsInArtifactsBucket.contents()) {
                s3Client.deleteObject(
                        DeleteObjectRequest.builder().bucket(TEST_COMPONENT_ARTIFACTS_S3_BUCKET).key(artifact.key())
                                .build());
            }
            s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(TEST_COMPONENT_ARTIFACTS_S3_BUCKET).build());
        } catch (NoSuchKeyException | NoSuchBucketException e) {
            // No-op
        } catch (S3Exception e) {
            logger.atInfo().addKeyValue("error-message", e.getMessage())
                    .log("Could not clean up test component artifacts");
        }
    }

    @SuppressWarnings("PMD.LinguisticNaming")
    protected CreateDeploymentResponse draftAndCreateDeployment(CreateDeploymentRequest createDeploymentRequest) {

        // update package name with random suffix to avoid conflict in cloud
        Map<String, ComponentDeploymentSpecification> updatedPkgMetadata = new HashMap<>();
        createDeploymentRequest.components()
                .forEach((key, val) -> updatedPkgMetadata.put(getTestComponentNameInCloud(key), val));
        createDeploymentRequest = createDeploymentRequest.toBuilder().components(updatedPkgMetadata).build();

        // set default value
        if (createDeploymentRequest.targetArn() == null) {
            createDeploymentRequest = createDeploymentRequest.toBuilder().targetArn(thingGroupArn).build();
        }
        if (createDeploymentRequest.deploymentPolicies() == null) {
            createDeploymentRequest = createDeploymentRequest.toBuilder().deploymentPolicies(
                    DeploymentPolicies.builder().configurationValidationPolicy(
                            DeploymentConfigurationValidationPolicy.builder().timeoutInSeconds(120).build())
                            .componentUpdatePolicy(DeploymentComponentUpdatePolicy.builder().action(NOTIFY_COMPONENTS)
                                    .timeoutInSeconds(120).build()).failureHandlingPolicy(DO_NOTHING).build()).build();
        }

        logger.atInfo().kv("CreateDeploymentRequest", createDeploymentRequest).log();
        CreateDeploymentResponse createDeploymentResult = greengrassClient.createDeployment(createDeploymentRequest);
        logger.atInfo().kv("CreateDeploymentResult", createDeploymentResult).log();

        // Keep track of deployments to clean up
        createdDeployments
                .add(CancelDeploymentRequest.builder().deploymentId(createDeploymentResult.deploymentId()).build());

        return createDeploymentResult;
    }

    protected void cleanup() {
        createdDeployments.forEach(greengrassClient::cancelDeployment);
        createdDeployments.clear();

        try {
            greengrassClient.deleteCoreDevice(
                    DeleteCoreDeviceRequest.builder().coreDeviceThingName(thingInfo.getThingName()).build());
        } catch (ResourceNotFoundException e) {
            logger.atDebug().kv("coreDevice", thingInfo.getThingName()).log("No core device to delete");
        }

        deviceProvisioningHelper.cleanThing(iotClient, thingInfo, false);
        createdThingGroups.forEach(thingGroup -> IotJobsUtils.cleanThingGroup(iotClient, thingGroupName));
        createdThingGroups.clear();

        if (kernel == null || kernel.getNucleusPaths().configPath() == null) {
            return;
        }
        for (File subFile : kernel.getNucleusPaths().configPath().toFile().listFiles()) {
            boolean result = subFile.delete();
            if (!result) {
                logger.atWarn().kv("fileName", subFile.toString()).log("Fail to delete file in cleanup.");
            }
        }
    }

    protected void setupTesRoleAndAlias() throws InterruptedException {
        deviceProvisioningHelper.setupIoTRoleForTes(TES_ROLE_NAME, TES_ROLE_ALIAS_NAME, thingInfo.getCertificateArn());
        if (tesRolePolicyArn == null || !tesRolePolicyArn.isPresent()) {
            tesRolePolicyArn = deviceProvisioningHelper
                    .createAndAttachRolePolicy(TES_ROLE_NAME, TES_ROLE_POLICY_NAME, TES_ROLE_POLICY_DOCUMENT,
                            GAMMA_REGION);
        }
    }

    protected static void setDeviceConfig(Kernel kernel, String key, Number value) {
        kernel.getConfig().lookup(SERVICES_NAMESPACE_TOPIC, DeviceConfiguration.DEFAULT_NUCLEUS_COMPONENT_NAME,
                CONFIGURATION_CONFIG_KEY, key).withValue(value);
    }

    @Override
    public void close() {
        greengrassClient.close();
        iotClient.close();
        iamClient.close();
        s3Client.close();
    }

    public static String getTestComponentNameInCloud(String name) {
        if (name.endsWith(testComponentSuffix) || name.contains("FakeLambda") || name.contains("Nucleus")) {
            return name;
        }
        return name + testComponentSuffix;
    }

}
