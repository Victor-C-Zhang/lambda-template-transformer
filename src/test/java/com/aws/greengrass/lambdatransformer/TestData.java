/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer;

import com.amazon.aws.iot.greengrass.component.common.ComponentConfiguration;
import com.amazon.aws.iot.greengrass.component.common.Platform;
import com.aws.greengrass.lambdatransformer.common.models.LambdaDeviceMount;
import com.aws.greengrass.lambdatransformer.common.models.LambdaEventSource;
import com.aws.greengrass.lambdatransformer.common.models.LambdaFilesystemPermission;
import com.aws.greengrass.lambdatransformer.common.models.LambdaInputPayloadEncodingType;
import com.aws.greengrass.lambdatransformer.common.models.LambdaIsolationMode;
import com.aws.greengrass.lambdatransformer.common.models.LambdaRuntime;
import com.aws.greengrass.lambdatransformer.common.models.LambdaTemplateParams;
import com.aws.greengrass.lambdatransformer.common.models.LambdaVolumeMount;
import com.aws.greengrass.lambdatransformer.common.models.TemplateContainerParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

public class TestData {
    public static String COMPONENT_NAME_1 = "component1";
    public static String LAMBDA_FUNCTION_ARN_1 = "arn:aws:lambda:us-west-2:111111111111:function:test-function_1";
    public static String LAMBDA_FUNCTION_HANDLER = "handler.function_handler";

    public static String EVENT_SOURCE_TOPIC_1 = "event-source-topic1";
    private static final Integer LAMBDA_TIMEOUT_IN_SECONDS = 30;
    private static final String DEVICE_PATH1 = "/dev/path1";

    private static final String VOLUME_DESTINATION1 = "/tmp/dst1";
    private static final String VOLUME_SOURCE = "/tmp/src1";

    private static final String LAMBDA_PARAM_EXEC_ARG = "-v";

    private static final String LAMBDA_PARAM_ENV_VAR_1 = "envvar1";
    private static final String LAMBDA_PARAM_ENV_VAL_1 = "evnval2";

    private static final int CONTAINER_PARAMS_MEMORY_SIZE = 1024;

    public static String COMPONENT_VERSION_STR_1 = "1.0.0";

    public static Platform all = Platform.builder().os(Platform.OS.ALL).architecture(Platform.Architecture.ALL).build();
    public static Platform windows = Platform.builder().os(Platform.OS.WINDOWS).build();

    public static Integer DEFAULT_INTEGER = 1;

    public static ComponentConfiguration COMPONENT_CONFIGURATION_1;

    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            COMPONENT_CONFIGURATION_1 = ComponentConfiguration.builder()
                    .defaultConfiguration(objectMapper.readTree("{ \"f1\":\"Hello\", \"f2\":\"World\" }"))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't process json", e);
        }
    }

    private static final LambdaEventSource COMPONENT_LAMBDA_EVENT_SOURCE_1 = LambdaEventSource.builder()
            .topic(EVENT_SOURCE_TOPIC_1)
            .build();

    private static final TemplateContainerParams CONTAINER_PARAMS = TemplateContainerParams.builder()
            .devices(Collections.singletonList(LambdaDeviceMount.builder()
                    .addGroupOwner(false)
                    .path(DEVICE_PATH1)
                    .permission(LambdaFilesystemPermission.RO)
                    .build()))
            .volumes(Collections.singletonList(LambdaVolumeMount.builder()
                    .destinationPath(VOLUME_DESTINATION1)
                    .sourcePath(VOLUME_SOURCE)
                    .permission(LambdaFilesystemPermission.RO)
                    .addGroupOwner(false)
                    .build()))
            .memorySizeInKB(CONTAINER_PARAMS_MEMORY_SIZE)
            .mountROSysfs(true)
            .build();

    public static LambdaTemplateParams.LambdaTemplateParamsBuilder LAMBDA_PARAMETERS_2 = LambdaTemplateParams.builder()
            .lambdaArn(LAMBDA_FUNCTION_ARN_1)
            .lambdaRuntime(LambdaRuntime.Python37)
            .lambdaHandler(LAMBDA_FUNCTION_HANDLER)
//            .platforms(Collections.singletonList(all))
//            .componentDependencies(dependencies)
            .eventSources(Collections.singletonList(COMPONENT_LAMBDA_EVENT_SOURCE_1))
            .timeoutInSeconds(LAMBDA_TIMEOUT_IN_SECONDS)
            .pinned(true)
            .inputPayloadEncodingType(LambdaInputPayloadEncodingType.JSON)
            .execArgs(Collections.singletonList(LAMBDA_PARAM_EXEC_ARG))
            .maxIdleTimeInSeconds(DEFAULT_INTEGER)
            .maxInstancesCount(DEFAULT_INTEGER)
            .maxQueueSize(DEFAULT_INTEGER)
            .statusTimeoutInSeconds(DEFAULT_INTEGER)
            .environmentVariables(Collections.singletonMap(LAMBDA_PARAM_ENV_VAR_1, LAMBDA_PARAM_ENV_VAL_1))
            .containerParams(CONTAINER_PARAMS)
            .containerMode(LambdaIsolationMode.NO_CONTAINER);
}
