package common.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum LambdaFilesystemPermission {
    @JsonProperty("ro")
    RO,
    @JsonProperty("rw")
    RW,
}
