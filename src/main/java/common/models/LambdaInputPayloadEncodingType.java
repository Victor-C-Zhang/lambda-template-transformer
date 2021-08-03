package common.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LambdaInputPayloadEncodingType {
    @JsonProperty("json")
    JSON,
    @JsonProperty("binary")
    BINARY,
}
