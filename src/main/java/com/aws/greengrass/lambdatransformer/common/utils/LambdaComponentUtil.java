/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.lambdatransformer.common.utils;

import com.aws.greengrass.lambdatransformer.common.models.LambdaRuntime;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.aws.greengrass.lambdatransformer.common.Constants.JAVA_LAMBDA_RUNTIME_COMMAND;
import static com.aws.greengrass.lambdatransformer.common.Constants.LAMBDA_EXEC_ARGS_HANDLER;
import static com.aws.greengrass.lambdatransformer.common.Constants.NODEJS_LAMBDA_RUNTIME_COMMAND;
import static com.aws.greengrass.lambdatransformer.common.Constants.PYTHON_LAMBDA_RUNTIME_COMMAND;


@SuppressWarnings("PMD.DoubleBraceInitialization")
public class LambdaComponentUtil {
    @SuppressFBWarnings("MS_MUTABLE_COLLECTION")
    public static final Map<LambdaRuntime, List<String>> RUNTIME_TO_EXEC_ARG_MAPPING = new HashMap<LambdaRuntime,
            List<String>>() {{
        put(LambdaRuntime.Python27, new LinkedList<>(
                Arrays.asList("python2.7", "-u", PYTHON_LAMBDA_RUNTIME_COMMAND, LAMBDA_EXEC_ARGS_HANDLER)));
        put(LambdaRuntime.Python37, new LinkedList<>(Arrays.asList("python3.7", "-u", PYTHON_LAMBDA_RUNTIME_COMMAND,
                LAMBDA_EXEC_ARGS_HANDLER)));
        put(LambdaRuntime.Python38, new LinkedList<>(Arrays.asList("python3.8", "-u", PYTHON_LAMBDA_RUNTIME_COMMAND,
                LAMBDA_EXEC_ARGS_HANDLER)));
        put(LambdaRuntime.Java8, new LinkedList<>(Arrays.asList("java8", JAVA_LAMBDA_RUNTIME_COMMAND,
                LAMBDA_EXEC_ARGS_HANDLER)));
        put(LambdaRuntime.Nodejs10X, new LinkedList<>(Arrays.asList("nodejs10.x", NODEJS_LAMBDA_RUNTIME_COMMAND,
                LAMBDA_EXEC_ARGS_HANDLER)));
        put(LambdaRuntime.Nodejs12X, new LinkedList<>(Arrays.asList("nodejs12.x", NODEJS_LAMBDA_RUNTIME_COMMAND,
                LAMBDA_EXEC_ARGS_HANDLER)));
    }};
}
