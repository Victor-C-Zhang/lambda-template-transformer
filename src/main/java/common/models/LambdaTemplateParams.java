package common.models;

import com.amazon.aws.iot.greengrass.component.common.DependencyProperties;
import com.amazon.aws.iot.greengrass.component.common.Platform;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static common.Constants.DEFAULT_LAMBDA_INPUT_PAYLOAD_ENCODING_TYPE;
import static common.Constants.DEFAULT_LAMBDA_ISOLATION_MODE;
import static common.Constants.LAMBDA_RECIPE_DEFAULT_MAX_IDLE_TIME_IN_SEC;
import static common.Constants.LAMBDA_RECIPE_DEFAULT_MAX_INSTANCE_COUNT;
import static common.Constants.LAMBDA_RECIPE_DEFAULT_MAX_QUEUE_SIZE;
import static common.Constants.LAMBDA_RECIPE_DEFAULT_PINNED;
import static common.Constants.LAMBDA_RECIPE_DEFAULT_STATUS_TIMEOUT_IN_SEC;
import static common.Constants.LAMBDA_RECIPE_DEFAULT_TIMEOUT_IN_SEC;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Similar, but not exactly equal to LambdaExecutionParameters in ComponentManagementService
public class LambdaTemplateParams {
    @NonNull
    private String lambdaArn;

    @NonNull
    private LambdaRuntime lambdaRuntime;

    @NonNull
    private String lambdaHandler;

    @JsonProperty("pubsubTopics")
    @Builder.Default
    private List<LambdaEventSource> eventSources = Collections.emptyList();

    @Builder.Default
    private Integer timeoutInSeconds = LAMBDA_RECIPE_DEFAULT_TIMEOUT_IN_SEC;

    @Builder.Default
    private Boolean pinned = LAMBDA_RECIPE_DEFAULT_PINNED;

    @Builder.Default
    private Integer statusTimeoutInSeconds = LAMBDA_RECIPE_DEFAULT_STATUS_TIMEOUT_IN_SEC;

    @Builder.Default
    private Integer maxQueueSize = LAMBDA_RECIPE_DEFAULT_MAX_QUEUE_SIZE;

    @Builder.Default
    private Integer maxInstancesCount = LAMBDA_RECIPE_DEFAULT_MAX_INSTANCE_COUNT;

    @Builder.Default
    private Integer maxIdleTimeInSeconds = LAMBDA_RECIPE_DEFAULT_MAX_IDLE_TIME_IN_SEC;

    @Builder.Default
    private LambdaInputPayloadEncodingType inputPayloadEncodingType = DEFAULT_LAMBDA_INPUT_PAYLOAD_ENCODING_TYPE;

    @Builder.Default
    private List<Platform> platforms = Collections.singletonList(Platform.builder().os(Platform.OS.ALL).build());

    @Builder.Default
    private Map<String, DependencyProperties> componentDependencies = Collections.emptyMap();

    @JsonProperty("lambdaArgs")
    @Builder.Default
    private List<String> execArgs = Collections.emptyList();

    @JsonProperty("lambdaEnvironmentVariables")
    @Builder.Default
    private Map<String, String> environmentVariables = Collections.emptyMap();

    @Builder.Default
    private LambdaIsolationMode containerMode = DEFAULT_LAMBDA_ISOLATION_MODE;

    @Builder.Default
    private TemplateContainerParams containerParams = TemplateContainerParams.builder().build();
}
