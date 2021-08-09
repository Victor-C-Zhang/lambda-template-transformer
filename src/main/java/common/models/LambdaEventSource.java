package common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambdaEventSource {
    private String topic;
    private LambdaEventSourceType type;

    private enum LambdaEventSourceType {
        @JsonProperty("pubsub")
        PUBSUB,
        @JsonProperty("iotcore")
        IOT_CORE,
    }
}
