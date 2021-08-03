package common.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LambdaIsolationMode {
    @JsonProperty("GreengrassContainer")
    GREENGRASS_CONTAINER,
    @JsonProperty("NoContainer")
    NO_CONTAINER,
}
