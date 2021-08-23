/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common;


import com.aws.greengrass.lambdatransformer.common.models.LambdaFilesystemPermission;
import com.aws.greengrass.lambdatransformer.common.models.LambdaInputPayloadEncodingType;
import com.aws.greengrass.lambdatransformer.common.models.LambdaIsolationMode;

import java.net.URI;

public class Constants {

    public static final String AWS_LAMBDA_PUBLISHER = "AWS Lambda";
    public static final LambdaIsolationMode DEFAULT_LAMBDA_ISOLATION_MODE = LambdaIsolationMode.GREENGRASS_CONTAINER;

    public static final LambdaFilesystemPermission DEFAULT_LAMBDA_FILE_SYSTEM_PERMISSION
            = LambdaFilesystemPermission.RO;
    public static final Boolean DEFAULT_ADD_GROUP_OWNER = Boolean.FALSE;
    public static final LambdaInputPayloadEncodingType DEFAULT_LAMBDA_INPUT_PAYLOAD_ENCODING_TYPE
            = LambdaInputPayloadEncodingType.JSON;

    public static final String PYTHON_LAMBDA_RUNTIME_COMMAND = "/runtime/python/lambda_runtime.py";
    public static final String JAVA_LAMBDA_RUNTIME_COMMAND = "com.amazonaws.greengrass.runtime.LambdaRuntime";
    public static final String NODEJS_LAMBDA_RUNTIME_COMMAND = "/runtime/nodejs/lambda_nodejs_runtime.js";
    public static final String LAMBDA_EXEC_ARGS_HANDLER = "--handler=";


    public static final String GREENGRASS_ARTIFACT_PREFIX = "greengrass:";
    public static final String LAMBDA_SETENV_CONTAINER_MODE_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_CONTAINER_MODE";
    public static final String LAMBDA_SETENV_ARN_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_ARN";
    public static final String LAMBDA_SETENV_HANDLER_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_FUNCTION_HANDLER";
    public static final String LAMBDA_SETENV_ARTIFACT_PATH_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_ARTIFACT_PATH";
    public static final String LAMBDA_SETENV_LAMBDA_PARAMS_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_PARAMS";
    public static final String LAMBDA_SETENV_LAMBDA_RUNTIME_PATH_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_RUNTIME_PATH";
    public static final String LAMBDA_SETENV_LAMBDA_RUNTIME_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_RUNTIME";
    public static final String LAMBDA_SETENV_CONTAINER_PARAMS_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_CONTAINER_PARAMS";
    public static final String LAMBDA_SETENV_ENCODING_TYPE_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_ENCODING_TYPE";
    public static final String LAMBDA_SETENV_STATUS_TIMEOUT_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_STATUS_TIMEOUT_SECONDS";
    public static final String LAMBDA_SETENV_EXEC_ARGS_PARAM_NAME = "AWS_GREENGRASS_LAMBDA_EXEC_ARGS";

    public static final String LAMBDA_LAUNCHER_DEPENDENCY_NAME = "aws.greengrass.LambdaLauncher";
    public static final String LAMBDA_RUNTIME_DEPENDENCY_NAME = "aws.greengrass.LambdaRuntimes";
    public static final String LAMBDA_TOKEN_EXCHANGE_SERVICE_DEPENDENCY_NAME = "aws.greengrass.TokenExchangeService";
    public static final String LAMBDA_TOKEN_EXCHANGE_SERVICE_VERSION_REQUIREMENTS = "^2.0.0";
    public static final String LAMBDA_LAUNCHER_DEPENDENCY_VERSION_REQUIREMENTS = "^2.0.0";
    public static final String LAMBDA_RUNTIME_DEPENDENCY_VERSION_REQUIREMENTS = "^2.0.0";

    public static final int LAMBDA_RECIPE_DEFAULT_MAX_IDLE_TIME_IN_SEC = 60;

    public static final int LAMBDA_RECIPE_DEFAULT_TIMEOUT_IN_SEC = 3;

    public static final int LAMBDA_RECIPE_DEFAULT_STATUS_TIMEOUT_IN_SEC = 60;

    public static final int LAMBDA_RECIPE_DEFAULT_MAX_QUEUE_SIZE = 1000;

    public static final int LAMBDA_RECIPE_DEFAULT_MAX_INSTANCE_COUNT = 100;

    public static final int LAMBDA_RECIPE_DEFAULT_LAMBDA_MEMORY_IN_KILO_BYTES = 16_000;

    public static final boolean LAMBDA_RECIPE_DEFAULT_PINNED = true;

    public static final String LAMBDA_ARTIFACT_NAME = "lambda-artifact";
    public static final String LAMBDA_ARTIFACT_EXTENSION = ".zip";
    public static final String LAMBDA_COMPLETE_ARTIFACT_NAME = LAMBDA_ARTIFACT_NAME + LAMBDA_ARTIFACT_EXTENSION;
    public static final URI LAMBDA_COMPLETE_ARTIFACT_URI =
            URI.create(GREENGRASS_ARTIFACT_PREFIX + LAMBDA_COMPLETE_ARTIFACT_NAME);

}
