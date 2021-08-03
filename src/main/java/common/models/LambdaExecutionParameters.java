package common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LambdaExecutionParameters {
    @JsonProperty("EnvironmentVariables")
    private Map<String, String> environmentalVariables;
}
