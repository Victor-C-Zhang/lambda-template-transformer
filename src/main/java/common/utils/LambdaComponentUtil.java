package common.utils;

import common.models.LambdaRuntime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static common.Constants.JAVA_LAMBDA_RUNTIME_COMMAND;
import static common.Constants.LAMBDA_EXEC_ARGS_HANDLER;
import static common.Constants.NODEJS_LAMBDA_RUNTIME_COMMAND;
import static common.Constants.PYTHON_LAMBDA_RUNTIME_COMMAND;

public class LambdaComponentUtil {
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
