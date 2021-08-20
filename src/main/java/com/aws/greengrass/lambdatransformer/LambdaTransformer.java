/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer;

import com.amazon.aws.iot.greengrass.component.common.ComponentArtifact;
import com.amazon.aws.iot.greengrass.component.common.ComponentConfiguration;
import com.amazon.aws.iot.greengrass.component.common.ComponentRecipe;
import com.amazon.aws.iot.greengrass.component.common.ComponentType;
import com.amazon.aws.iot.greengrass.component.common.DependencyProperties;
import com.amazon.aws.iot.greengrass.component.common.DependencyType;
import com.amazon.aws.iot.greengrass.component.common.Platform;
import com.amazon.aws.iot.greengrass.component.common.PlatformSpecificManifest;
import com.amazon.aws.iot.greengrass.component.common.RecipeFormatVersion;
import com.amazon.aws.iot.greengrass.component.common.Unarchive;
import com.aws.greengrass.deployment.templating.RecipeTransformer;
import com.aws.greengrass.deployment.templating.exceptions.RecipeTransformerException;
import com.aws.greengrass.lambdatransformer.common.models.ContainerParams;
import com.aws.greengrass.lambdatransformer.common.models.DefaultConfiguration;
import com.aws.greengrass.lambdatransformer.common.models.LambdaDeviceMount;
import com.aws.greengrass.lambdatransformer.common.models.LambdaEventSource;
import com.aws.greengrass.lambdatransformer.common.models.LambdaExecutionParameters;
import com.aws.greengrass.lambdatransformer.common.models.LambdaRuntime;
import com.aws.greengrass.lambdatransformer.common.models.LambdaTemplateParams;
import com.aws.greengrass.lambdatransformer.common.models.LambdaVolumeMount;
import com.aws.greengrass.lambdatransformer.common.models.LifecycleType;
import com.aws.greengrass.lambdatransformer.common.utils.LambdaComponentUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aws.greengrass.lambdatransformer.common.Constants.AWS_LAMBDA_PUBLISHER;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_ARTIFACT_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_COMPLETE_ARTIFACT_URI;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_EXEC_ARGS_HANDLER;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_LAUNCHER_DEPENDENCY_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_LAUNCHER_DEPENDENCY_VERSION_REQUIREMENTS;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_RUNTIME_DEPENDENCY_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_RUNTIME_DEPENDENCY_VERSION_REQUIREMENTS;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_ARN_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_ARTIFACT_PATH_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_CONTAINER_MODE_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_CONTAINER_PARAMS_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_ENCODING_TYPE_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_EXEC_ARGS_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_HANDLER_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_LAMBDA_PARAMS_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_LAMBDA_RUNTIME_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_LAMBDA_RUNTIME_PATH_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_SETENV_STATUS_TIMEOUT_PARAM_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_TOKEN_EXCHANGE_SERVICE_DEPENDENCY_NAME;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_TOKEN_EXCHANGE_SERVICE_VERSION_REQUIREMENTS;

public class LambdaTransformer extends RecipeTransformer {

    private final ObjectMapper mapper = new ObjectMapper();

    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    @Override
    protected String initTemplateSchema() {
        return "lambdaArn:\n" + "  type: \"string\"\n" + "  required: true\n" + "lambdaRuntime:\n"
                + "  type: \"string\"\n" + "  required: true\n" + "lambdaHandler:\n" + "  type: \"string\"\n"
                + "  required: true\n" + "maxIdleTimeInSeconds:\n" + "  type: \"number\"\n" + "  required: false\n"
                + "  defaultValue: 60\n" + "lambdaEnvironmentVariables:\n" + "  type: \"object\"\n"
                + "  required: false\n" + "  defaultValue: {}\n" + "pinned:\n" + "  type: \"boolean\"\n"
                + "  required: false\n" + "  defaultValue: true\n" + "lambdaArgs:\n" + "  type: \"array\"\n"
                + "  required: false\n" + "  defaultValue: []\n" + "maxInstancesCount:\n" + "  type: \"number\"\n"
                + "  required: false\n" + "  defaultValue: 100\n" + "pubsubTopics:\n" + "  type: \"array\"\n"
                + "  required: false\n" + "  defaultValue: []\n" + "containerParams:\n" + "  type: \"object\"\n"
                + "  required: false\n" + "  defaultValue:\n" + "    mountROSysfs: false\n" + "    volumes: []\n"
                + "    devices: []\n" + "    memorySize: 16000\n" + "timeoutInSeconds:\n" + "  type: \"number\"\n"
                + "  required: false\n" + "  defaultValue: 3\n" + "platforms:\n" + "  type: \"array\"\n"
                + "  required: false\n" + "  defaultValue:\n" + "  - os: \"*\"\n" + "componentDependencies:\n"
                + "  type: \"object\"\n" + "  required: false\n" + "  defaultValue: {}\n" + "maxQueueSize:\n"
                + "  type: \"number\"\n" + "  required: false\n" + "  defaultValue: 1000\n"
                + "statusTimeoutInSeconds:\n" + "  type: \"number\"\n" + "  required: false\n" + "  defaultValue: 60\n"
                + "inputPayloadEncodingType:\n" + "  type: \"string\"\n" + "  required: false\n"
                + "  defaultValue: \"json\"\n" + "containerMode:\n" + "  type: \"string\"\n" + "  required: false\n"
                + "  defaultValue: \"GreengrassContainer\"\n";
    }

    @Override
    protected Class<?> initRecievingClass() {
        return LambdaTemplateParams.class;
    }

    @Override
    public ComponentRecipe transform(ComponentRecipe paramFile, Object componentParamsObj)
            throws RecipeTransformerException {
        LambdaTemplateParams lambdaParameters = (LambdaTemplateParams) componentParamsObj;

        List<Platform> componentPlatforms = lambdaParameters.getPlatforms();
        if (componentPlatforms == null || componentPlatforms.isEmpty()) {
            throw new RecipeTransformerException("At least one platform is expected to be set by caller");
        }

        List<String> execArgs = getExecArgsSpecificToLambdaRuntime(lambdaParameters.getLambdaHandler(),
                lambdaParameters.getLambdaRuntime());

        if (lambdaParameters.getExecArgs() != null) {
            execArgs.addAll(lambdaParameters.getExecArgs());
        }

        // the same lifecycle exists per platform
        Map<String, Object> lifecycleMap;
        try {
            lifecycleMap = getLifecycleFromLambda(
                    lambdaParameters.getLambdaArn(),
                    lambdaParameters.getLambdaHandler(),
                    lambdaParameters.getLambdaRuntime(),
                    execArgs
            );
        } catch (JsonProcessingException e) {
            throw new RecipeTransformerException(e);
        }

        ComponentConfiguration configuration = createConfigurationFromParameters(lambdaParameters);

        //
        // Creates an initial set of read-modify-write PlatformData. Additional functions can edit the PlatformData
        // that is then used to produce all the manifests
        //
        List<PlatformData> perPlatform = lambdaParameters.getPlatforms().stream().map(platform -> {
            PlatformData datum = new PlatformData();
            datum.setPlatform(platform);
            return datum;
        }).collect(Collectors.toList());

        List<ComponentArtifact> componentArtifactData = Collections.singletonList(ComponentArtifact.builder()
                .uri(LAMBDA_COMPLETE_ARTIFACT_URI)
                .unarchive(Unarchive.ZIP)
                .build());
        perPlatform.forEach(datum -> datum.setArtifacts(componentArtifactData));

        Map<String, DependencyProperties> dependenciesMap = lambdaParameters.getComponentDependencies();
        addLambdaDependencies(dependenciesMap);

        List<PlatformSpecificManifest> manifests = perPlatform
                .stream()
                .map(PlatformData::buildManifest)
                .collect(Collectors.toList());

        return ComponentRecipe.builder()
                .recipeFormatVersion(RecipeFormatVersion.JAN_25_2020)
                .componentName(paramFile.getComponentName())
                .componentDependencies(dependenciesMap)
                .componentVersion(paramFile.getComponentVersion())
                .componentDescription(paramFile.getComponentDescription())
                .componentPublisher(AWS_LAMBDA_PUBLISHER)
                .componentSource(lambdaParameters.getLambdaArn())
                .manifests(manifests)
                .componentConfiguration(configuration)
                .componentType(ComponentType.LAMBDA)
                .lifecycle(lifecycleMap)
                .build();
    }

    private ComponentConfiguration createConfigurationFromParameters(LambdaTemplateParams lambdaParameters) {

        Map<String, LambdaDeviceMount> devices = getDevices(lambdaParameters);
        Map<String, LambdaVolumeMount> volumes = getVolumes(lambdaParameters);
        Map<String, LambdaEventSource> pubSubTopics = getPubSubTopics(lambdaParameters);

        DefaultConfiguration defaultConfiguration = DefaultConfiguration
                .builder()
                .containerMode(lambdaParameters.getContainerMode())
                .lambdaExecutionParameters(
                        LambdaExecutionParameters
                                .builder()
                                .environmentalVariables(lambdaParameters.getEnvironmentVariables())
                                .build())
                .containerParams(
                        ContainerParams
                                .builder()
                                .devices(devices)
                                .volumes(volumes)
                                .memorySizeInKB(lambdaParameters.getContainerParams().getMemorySizeInKB())
                                .mountROSysfs(lambdaParameters.getContainerParams().getMountROSysfs())
                                .build())
                .timeoutInSeconds(lambdaParameters.getTimeoutInSeconds())
                .statusTimeoutInSeconds(lambdaParameters.getStatusTimeoutInSeconds())
                .pinned(lambdaParameters.getPinned())
                .inputPayloadEncodingType(lambdaParameters.getInputPayloadEncodingType())
                .pubsubTopics(pubSubTopics)
                .maxInstancesCount(lambdaParameters.getMaxInstancesCount())
                .maxQueueSize(lambdaParameters.getMaxQueueSize())
                .maxIdleTimeInSeconds(lambdaParameters.getMaxIdleTimeInSeconds())
                .build();


        return ComponentConfiguration
                .builder()
                .defaultConfiguration(mapper.convertValue(defaultConfiguration, JsonNode.class))
                .build();
    }


    private Map<String, LambdaDeviceMount> getDevices(LambdaTemplateParams lambdaParameters) {
        Map<String, LambdaDeviceMount> devices = new HashMap<>();

        if (lambdaParameters.getContainerParams().getDevices() == null) {
            return devices;
        }

        for (int i = 0; i < lambdaParameters.getContainerParams().getDevices().size(); i++) {
            devices.put(Integer.toString(i), lambdaParameters.getContainerParams().getDevices().get(i));
        }
        return devices;
    }

    private Map<String, LambdaVolumeMount> getVolumes(LambdaTemplateParams lambdaParameters) {
        Map<String, LambdaVolumeMount> volumes = new HashMap<>();

        if (lambdaParameters.getContainerParams().getVolumes() == null) {
            return volumes;
        }

        for (int i = 0; i < lambdaParameters.getContainerParams().getVolumes().size(); i++) {
            volumes.put(Integer.toString(i), lambdaParameters.getContainerParams().getVolumes().get(i));
        }

        return volumes;
    }

    private Map<String, LambdaEventSource> getPubSubTopics(LambdaTemplateParams lambdaParameters) {
        Map<String, LambdaEventSource> individualPubSubTopicMap = new HashMap<>();

        if (lambdaParameters.getEventSources() == null || lambdaParameters.getEventSources().isEmpty()) {
            return individualPubSubTopicMap;
        }

        for (int i = 0; i < lambdaParameters.getEventSources().size(); i++) {
            individualPubSubTopicMap.put(Integer.toString(i), lambdaParameters.getEventSources().get(i));
        }

        return individualPubSubTopicMap;
    }

    private static List<String> getExecArgsSpecificToLambdaRuntime(String handlerName, LambdaRuntime runtime)
            throws RecipeTransformerException {
        List<String> execArgs;
        String handlerArgument = LAMBDA_EXEC_ARGS_HANDLER + handlerName;
        switch (runtime) {
            case Python27:
                execArgs = new ArrayList<>(LambdaComponentUtil.RUNTIME_TO_EXEC_ARG_MAPPING.get(LambdaRuntime.Python27));
                execArgs.remove(3);
                execArgs.add(handlerArgument);
                break;
            case Python37:
                execArgs = new ArrayList<>(LambdaComponentUtil.RUNTIME_TO_EXEC_ARG_MAPPING.get(LambdaRuntime.Python37));
                execArgs.remove(3);
                execArgs.add(handlerArgument);
                break;
            case Python38:
                execArgs = new ArrayList<>(LambdaComponentUtil.RUNTIME_TO_EXEC_ARG_MAPPING.get(LambdaRuntime.Python38));
                execArgs.remove(3);
                execArgs.add(handlerArgument);
                break;
            case Java8:
                execArgs = new ArrayList<>(LambdaComponentUtil.RUNTIME_TO_EXEC_ARG_MAPPING.get(LambdaRuntime.Java8));
                execArgs.remove(2);
                execArgs.add(handlerArgument);
                break;
            case Nodejs10X:
                execArgs =
                        new ArrayList<>(LambdaComponentUtil.RUNTIME_TO_EXEC_ARG_MAPPING.get(LambdaRuntime.Nodejs10X));
                execArgs.remove(2);
                execArgs.add(handlerArgument);
                break;
            case Nodejs12X:
                execArgs =
                        new ArrayList<>(LambdaComponentUtil.RUNTIME_TO_EXEC_ARG_MAPPING.get(LambdaRuntime.Nodejs12X));
                execArgs.remove(2);
                execArgs.add(handlerArgument);
                break;
            default:
                throw new RecipeTransformerException("The provided runtime for this lambdaFunction : "
                        + runtime + " is not supported by Greengrass currently.");
        }
        return execArgs;
    }

    private void addLambdaDependencies(Map<String, DependencyProperties> mapToAddTo) {
        mapToAddTo.put(LAMBDA_LAUNCHER_DEPENDENCY_NAME,
                DependencyProperties.builder().versionRequirement(LAMBDA_LAUNCHER_DEPENDENCY_VERSION_REQUIREMENTS)
                        .dependencyType(DependencyType.HARD).build());
        mapToAddTo.put(LAMBDA_RUNTIME_DEPENDENCY_NAME,
                DependencyProperties.builder().versionRequirement(LAMBDA_RUNTIME_DEPENDENCY_VERSION_REQUIREMENTS)
                        .dependencyType(DependencyType.SOFT).build());
        mapToAddTo.put(LAMBDA_TOKEN_EXCHANGE_SERVICE_DEPENDENCY_NAME,
                DependencyProperties.builder().versionRequirement(LAMBDA_TOKEN_EXCHANGE_SERVICE_VERSION_REQUIREMENTS)
                        .dependencyType(DependencyType.HARD).build());
    }

    private Map<String, Object> getLifecycleFromLambda(String lambdaArn, String handlerName,
                                                       LambdaRuntime runtime,
                                                       List<String> execArgs) throws JsonProcessingException {

        Map<String, Object> individualLifeCycle = new HashMap<>();
        individualLifeCycle.put(LifecycleType.setenv.toString().toLowerCase(), new HashMap<String, Object>());

        Map<String, String> setenvIndividualMap =
                (Map<String, String>) individualLifeCycle.get(LifecycleType.setenv.toString().toLowerCase());
        setenvIndividualMap.put(LAMBDA_SETENV_CONTAINER_MODE_PARAM_NAME, "{configuration:/containerMode}");
        setenvIndividualMap.put(LAMBDA_SETENV_ARN_PARAM_NAME, lambdaArn);
        setenvIndividualMap.put(LAMBDA_SETENV_HANDLER_PARAM_NAME, handlerName);
        setenvIndividualMap.put(LAMBDA_SETENV_ARTIFACT_PATH_PARAM_NAME, "{artifacts:decompressedPath}/"
                + LAMBDA_ARTIFACT_NAME);
        setenvIndividualMap.put(LAMBDA_SETENV_LAMBDA_PARAMS_PARAM_NAME, "{configuration:/lambdaExecutionParameters}");
        setenvIndividualMap.put(LAMBDA_SETENV_LAMBDA_RUNTIME_PATH_PARAM_NAME,
                "{" + LAMBDA_RUNTIME_DEPENDENCY_NAME + ":artifacts:decompressedPath}/runtime/");
        setenvIndividualMap.put(LAMBDA_SETENV_LAMBDA_RUNTIME_PARAM_NAME, runtime.toString());
        setenvIndividualMap.put(LAMBDA_SETENV_CONTAINER_PARAMS_PARAM_NAME, "{configuration:/containerParams}");
        setenvIndividualMap.put(LAMBDA_SETENV_ENCODING_TYPE_PARAM_NAME, "{configuration:/inputPayloadEncodingType}");
        setenvIndividualMap.put(LAMBDA_SETENV_STATUS_TIMEOUT_PARAM_NAME, "{configuration:/statusTimeoutInSeconds}");
        setenvIndividualMap.put(LAMBDA_SETENV_EXEC_ARGS_PARAM_NAME, mapper.writeValueAsString(execArgs));

        Map<String, Object> startup = new HashMap<>();
        individualLifeCycle.put(LifecycleType.startup.toString(), startup);
        startup.put("requiresPrivilege", true);
        startup.put("script", "{" + LAMBDA_LAUNCHER_DEPENDENCY_NAME + ":artifacts:path}/lambda-launcher start");

        Map<String, Object> shutdown = new HashMap<>();
        individualLifeCycle.put(LifecycleType.shutdown.toString(), shutdown);
        shutdown.put("requiresPrivilege", true);
        shutdown.put("script", "{" + LAMBDA_LAUNCHER_DEPENDENCY_NAME + ":artifacts:path}/lambda-launcher stop;"
                + " {" + LAMBDA_LAUNCHER_DEPENDENCY_NAME + ":artifacts:path}/lambda-launcher clean");

        return individualLifeCycle;
    }

    /**
     * Temporary structure for collecting data for each platform.
     */
    @Data
    protected static class PlatformData {
        //
        // PlatformSpecificManifest.builder does not allow reading fields previously written. This structure
        // permits reading of partial data, and storing any additional intermediate data (both not used yet)
        //
        private Platform platform;
        private String name;
        private List<ComponentArtifact> artifacts;

        /**
         * Construct a manifest from the (potentially subset of) mutable data.
         * @return Platform specific manifest
         */
        public PlatformSpecificManifest buildManifest() {
            return PlatformSpecificManifest.builder()
                    .platform(platform)
                    .name(name)
                    .artifacts(artifacts)
                    .build();
        }
    }
}
