/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.utils;

import com.aws.greengrass.lambdatransformer.common.models.LambdaRuntime;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aws.greengrass.lambdatransformer.common.Constants.JAVA_LAMBDA_RUNTIME_COMMAND;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_EXEC_ARGS_HANDLER;
import static com.aws.greengrass.lambdatransformer.common.Constants.NODEJS_LAMBDA_RUNTIME_COMMAND;
import static com.aws.greengrass.lambdatransformer.common.Constants.PYTHON_LAMBDA_RUNTIME_COMMAND;


public class LambdaComponentUtil {
    public static final Map<LambdaRuntime, List<String>> RUNTIME_TO_EXEC_ARG_MAPPING;

    static {
        HashMap<LambdaRuntime, List<String>> tempRuntimeMap = new HashMap<>();
        tempRuntimeMap.put(LambdaRuntime.Python27, Collections.unmodifiableList(
                Arrays.asList("python2.7", "-u", PYTHON_LAMBDA_RUNTIME_COMMAND, LAMBDA_EXEC_ARGS_HANDLER)));
        tempRuntimeMap.put(LambdaRuntime.Python37, Collections.unmodifiableList(Arrays.asList("python3.7", "-u",
                PYTHON_LAMBDA_RUNTIME_COMMAND, LAMBDA_EXEC_ARGS_HANDLER)));
        tempRuntimeMap.put(LambdaRuntime.Python38, Collections.unmodifiableList(Arrays.asList("python3.8", "-u",
                PYTHON_LAMBDA_RUNTIME_COMMAND, LAMBDA_EXEC_ARGS_HANDLER)));
        tempRuntimeMap.put(LambdaRuntime.Java8, Collections.unmodifiableList(Arrays.asList("java8",
                JAVA_LAMBDA_RUNTIME_COMMAND,
                LAMBDA_EXEC_ARGS_HANDLER)));
        tempRuntimeMap.put(LambdaRuntime.Nodejs10X, Collections.unmodifiableList(Arrays.asList("nodejs10.x",
                NODEJS_LAMBDA_RUNTIME_COMMAND, LAMBDA_EXEC_ARGS_HANDLER)));
        tempRuntimeMap.put(LambdaRuntime.Nodejs12X, Collections.unmodifiableList(Arrays.asList("nodejs12.x",
                NODEJS_LAMBDA_RUNTIME_COMMAND, LAMBDA_EXEC_ARGS_HANDLER)));
        RUNTIME_TO_EXEC_ARG_MAPPING = Collections.unmodifiableMap(tempRuntimeMap);
    }
}
