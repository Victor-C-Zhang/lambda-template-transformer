package common.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerParams {
    private Integer memorySizeInKB;
    private Boolean mountROSysfs;
    private Map<String, LambdaVolumeMount> volumes;
    private Map<String, LambdaDeviceMount> devices;
}
