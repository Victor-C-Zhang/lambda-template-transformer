package common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultConfiguration {

    private LambdaExecutionParameters lambdaExecutionParameters;

    private ContainerParams containerParams;

    private LambdaIsolationMode containerMode;

    private int timeoutInSeconds;

    private int maxInstancesCount;

    private LambdaInputPayloadEncodingType inputPayloadEncodingType;

    private int maxQueueSize;

    private boolean pinned;

    private int maxIdleTimeInSeconds;

    private int statusTimeoutInSeconds;

    private Map<String, LambdaEventSource> pubsubTopics;
}

