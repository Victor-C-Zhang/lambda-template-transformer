package common;

import common.models.LambdaFilesystemPermission;
import common.models.LambdaInputPayloadEncodingType;
import common.models.LambdaIsolationMode;
import common.models.Range;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public class Constants {
    //Service
    public static final String SERVICE_NAME = "EvergreenComponentManagementService";

    //Operations
    public static final String CREATE_COMPONENT_OPERATION = "CreateComponent";
    public static final String CREATE_COMPONENT_VERSION_OPERATION = "CreateComponentVersion";
    public static final String DELETE_COMPONENT_OPERATION = "DeleteComponent";
    public static final String DESCRIBE_COMPONENT_OPERATION = "DescribeComponent";
    public static final String GET_COMPONENT_VERSION_ARTIFACT_OPERATION = "GetComponentVersionArtifact";
    public static final String GET_COMPONENT_OPERATION = "GetComponent";
    public static final String LIST_COMPONENTS_OPERATION = "ListComponents";
    public static final String LIST_COMPONENT_VERSIONS_OPERATION = "ListComponentVersions";
    public static final String CREATE_COMPONENT_FROM_LAMBDA = "CreateComponentFromLambda";
    public static final String RESOLVE_COMPONENT_CANDIDATES_OPERATION = "ResolveComponentCandidates";
    public static final String RESOLVE_COMPONENT_VERSIONS_OPERATION = "ResolveComponentVersions";
    public static final String CREATE_PUBLIC_COMPONENT_VERSION_OPERATION = "CreatePublicComponentVersion";

    //Metrics
    public static final String COMPONENT_DATA_ACCESS_GET = "ComponentDdbDataAccess.get";
    public static final String COMPONENT_DATA_ACCESS_FIND = "ComponentDdbDataAccess.find";
    public static final String COMPONENT_DATA_ACCESS_CREATE = "ComponentDdbDataAccess.create";
    public static final String COMPONENT_DATA_ACCESS_LIST = "ComponentDdbDataAccess.list";
    public static final String COMPONENT_DATA_ACCESS_EXIST = "ComponentDdbDataAccess.exist";
    public static final String COMPONENT_DATA_ACCESS_DELETE = "ComponentDdbDataAccess.delete";
    public static final String COMPONENT_DATA_ACCESS_UPDATE = "ComponentDdbDataAccess.update";
    public static final String COMPONENT_LATEST_VERSION_DATA_ACCESS_GET = "ComponentLatestVersionDdbDataAccess.get";
    public static final String COMPONENT_LATEST_VERSION_DATA_ACCESS_FIND = "ComponentLatestVersionDdbDataAccess.find";
    public static final String COMPONENT_LATEST_VERSION_DATA_ACCESS_CREATE = "ComponentLatestVersionDdbDataAccess.create";
    public static final String COMPONENT_LATEST_VERSION_DATA_ACCESS_LIST = "ComponentLatestVersionDdbDataAccess.list";
    public static final String COMPONENT_LATEST_VERSION_DATA_ACCESS_EXIST = "ComponentLatestVersionDdbDataAccess.exist";
    public static final String COMPONENT_LATEST_VERSION_DATA_ACCESS_DELETE = "ComponentLatestVersionDdbDataAccess.delete";
    public static final String COMPONENT_LATEST_VERSION_DATA_ACCESS_UPDATE = "ComponentLatestVersionDdbDataAccess.update";
    public static final String ARTIFACT_DATA_ACCESS_GET = "ArtifactDdbDataAccess.get";
    public static final String ARTIFACT_DATA_ACCESS_FIND = "ArtifactDdbDataAccess.find";
    public static final String ARTIFACT_DATA_ACCESS_CREATE = "ArtifactDdbDataAccess.create";
    public static final String ARTIFACT_DATA_ACCESS_DELETE = "ArtifactDdbDataAccess.delete";
    public static final String ARTIFACT_DATA_ACCESS_UPDATE = "ArtifactDdbDataAccess.update";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_GET = "PublicComponentDdbDataAccess.get";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_FIND = "PublicComponentDdbDataAccess.find";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_CREATE = "PublicComponentDdbDataAccess.create";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_LIST = "PublicComponentDdbDataAccess.list";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_LIST_LATEST_COMPONENTS = "PublicComponentDdbDataAccess.list.latestversion";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_EXIST = "PublicComponentDdbDataAccess.exist";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_DEPLOYABLE_EXIST = "PublicComponentDdbDataAccess.exist.deployable";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_DELETE = "PublicComponentDdbDataAccess.delete";
    public static final String PUBLIC_COMPONENT_DATA_ACCESS_UPDATE = "PublicComponentDdbDataAccess.update";
    public static final String PUBLIC_COMPONENT_LATEST_VERSION_DATA_ACCESS_GET = "PublicComponentLatestVersionDdbDataAccess.get";
    public static final String PUBLIC_COMPONENT_LATEST_VERSION_DATA_ACCESS_FIND = "PublicComponentLatestVersionDdbDataAccess.find";
    public static final String PUBLIC_COMPONENT_LATEST_VERSION_DATA_ACCESS_CREATE = "PublicComponentLatestVersionDdbDataAccess.create";
    public static final String PUBLIC_COMPONENT_LATEST_VERSION_DATA_ACCESS_LIST = "PublicComponentLatestVersionDdbDataAccess.list";
    public static final String PUBLIC_COMPONENT_LATEST_VERSION_DATA_ACCESS_EXIST = "PublicComponentLatestVersionDdbDataAccess.exist";
    public static final String PUBLIC_COMPONENT_LATEST_VERSION_DATA_ACCESS_DELETE = "PublicComponentLatestVersionDdbDataAccess.delete";
    public static final String PUBLIC_COMPONENT_LATEST_VERSION_DATA_ACCESS_UPDATE = "PublicComponentLatestVersionDdbDataAccess.update";

    public static final String ARTIFACT_BUCKET_NAME_ENVIRONMENT_VARIABLE_NAME = "ARTIFACT_BUCKET_NAME";
    public static final String PUBLIC_COMPONENT_ACCOUNT_ID_ARTIFACT_TABLE = "public";
    public static final String ARTIFACT_S3_KEY_FORMAT = "%s/%s/%s/%s";
    public static final String PUBLIC_COMPONENT_ARTIFACT_S3_KEY_FORMAT = "public/%s/%s/s3/%s/%s";
    public static final String PUBLIC_COMPONENT_SFN_ARN_ENVIRONMENT_VARIABLE_NAME = "PUBLIC_COMPONENT_WF_STATE_MACHINE_ARN";

    public static final int DEFAULT_MAX_RESULT = 100;
    public static final String KEY_SEPARATOR = "::";

    public static final String AWS_LAMBDA_PUBLISHER = "AWS Lambda";
    public static final LambdaIsolationMode DEFAULT_LAMBDA_ISOLATION_MODE = LambdaIsolationMode.GREENGRASS_CONTAINER;

    public static final LambdaFilesystemPermission DEFAULT_LAMBDA_FILE_SYSTEM_PERMISSION = LambdaFilesystemPermission.RO;
    public static final Boolean DEFAULT_ADD_GROUP_OWNER = Boolean.FALSE;
    public static final LambdaInputPayloadEncodingType DEFAULT_LAMBDA_INPUT_PAYLOAD_ENCODING_TYPE = LambdaInputPayloadEncodingType.JSON;
    public static final String VALID_PATH_PREFIX_FOR_DEVICES = "/dev";

    // Most Linux file systems have a maximum path length limit 4096 characters.
    public static final int PATH_MAX = 4096;

    // Most Linux file systems have a maximum file length limit 255 characters.
    public static final int FILENAME_MAX = 255;

    public static final List<String> INVALID_VOLUME_SOURCE_PATHS = Collections.singletonList("/sys");
    public static final List<String> INVALID_VOLUME_DEST_PATHS = Collections.singletonList("/proc");

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

    public static final int DEFAULT_MAX_INT_VALUE = 2147483647;

    public static final int LAMBDA_RECIPE_DEFAULT_MAX_IDLE_TIME_IN_SEC = 60;
    public static final int LAMBDA_RECIPE_MINIMUM_MAX_IDLE_TIME_IN_SEC = 30;

    public static final int LAMBDA_RECIPE_DEFAULT_TIMEOUT_IN_SEC = 3;
    public static final int LAMBDA_RECIPE_MINIMUM_TIMEOUT_IN_SEC = 1;

    public static final int LAMBDA_RECIPE_DEFAULT_STATUS_TIMEOUT_IN_SEC = 60;
    public static final int LAMBDA_RECIPE_MINIMUM_STATUS_TIMEOUT_IN_SEC = 30;

    public static final int LAMBDA_RECIPE_DEFAULT_MAX_QUEUE_SIZE = 1000;
    public static final int LAMBDA_RECIPE_MINIMUM_MAX_QUEUE_SIZE = 1;

    public static final int LAMBDA_RECIPE_DEFAULT_MAX_INSTANCE_COUNT = 100;
    public static final int LAMBDA_RECIPE_MINIMUM_MAX_INSTANCE_COUNT = 1;

    public static final int LAMBDA_RECIPE_DEFAULT_LAMBDA_MEMORY_IN_KILO_BYTES = 16384; // 16 MB
    public static final int LAMBDA_RECIPE_MINIMUM_LAMBDA_MEMORY_IN_KILO_BYTES = 2048; // 2048 KB

    public static final boolean LAMBDA_RECIPE_DEFAULT_PINNED = true;

    public static final Range LAMBDA_RECIPE_TIMEOUT_RANGE = Range
            .builder()
            .min(LAMBDA_RECIPE_MINIMUM_TIMEOUT_IN_SEC)
            .max(DEFAULT_MAX_INT_VALUE)
            .build();

    public static final Range LAMBDA_RECIPE_STATUS_TIMEOUT_RANGE = Range
            .builder()
            .min(LAMBDA_RECIPE_MINIMUM_STATUS_TIMEOUT_IN_SEC)
            .max(DEFAULT_MAX_INT_VALUE)
            .build();

    public static final Range LAMBDA_RECIPE_MAX_QUEUE_SIZE_RANGE = Range
            .builder()
            .min(LAMBDA_RECIPE_MINIMUM_MAX_QUEUE_SIZE)
            .max(DEFAULT_MAX_INT_VALUE)
            .build();

    public static final Range LAMBDA_RECIPE_MAX_INSTANCE_COUNT_RANGE = Range
            .builder()
            .min(LAMBDA_RECIPE_MINIMUM_MAX_INSTANCE_COUNT)
            .max(DEFAULT_MAX_INT_VALUE)
            .build();

    public static final Range LAMBDA_RECIPE_MAX_IDLE_TIME_RANGE = Range
            .builder()
            .min(LAMBDA_RECIPE_MINIMUM_MAX_IDLE_TIME_IN_SEC)
            .max(DEFAULT_MAX_INT_VALUE)
            .build();

    public static final Range LAMBDA_RECIPE_LAMBDA_MEMORY_RANGE = Range
            .builder()
            .min(LAMBDA_RECIPE_MINIMUM_LAMBDA_MEMORY_IN_KILO_BYTES)
            .max(DEFAULT_MAX_INT_VALUE)
            .build();

    public static final String LAMBDA_ARTIFACT_NAME = "lambda-artifact";
    public static final String LAMBDA_ARTIFACT_EXTENSION = ".zip";
    public static final String LAMBDA_COMPLETE_ARTIFACT_NAME = LAMBDA_ARTIFACT_NAME + LAMBDA_ARTIFACT_EXTENSION;
    public static final URI LAMBDA_COMPLETE_ARTIFACT_URI = URI.create(GREENGRASS_ARTIFACT_PREFIX + LAMBDA_COMPLETE_ARTIFACT_NAME);

    //ARN constants
    public static final String PUBLIC_COMPONENT_ACCOUNT_ID = "aws";
}
