package common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

import static common.Constants.LAMBDA_RECIPE_DEFAULT_LAMBDA_MEMORY_IN_KILO_BYTES;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateContainerParams {

    @Builder.Default
    private Integer memorySizeInKB = LAMBDA_RECIPE_DEFAULT_LAMBDA_MEMORY_IN_KILO_BYTES;

    @Builder.Default
    private Boolean mountROSysfs = true;

    @Builder.Default
    private List<LambdaVolumeMount> volumes = Collections.emptyList();

    @Builder.Default
    private List<LambdaDeviceMount> devices = Collections.emptyList();
}
