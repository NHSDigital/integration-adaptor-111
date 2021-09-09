package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Device;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.adaptors.oneoneone.config.OneOneOneProperties;

@Component
@AllArgsConstructor
public class DeviceMapper {
    private static final String DEVICE_MODEL_NAME = "111 Adaptor";

    private final ResourceUtil resourceUtil;
    private final OneOneOneProperties oneOneOneProperties;

    public Device mapDevice() {
        Device device = new Device();

        device.setIdElement(resourceUtil.newRandomUuid());
        device.setVersion(oneOneOneProperties.getVersion())
            .setModel(DEVICE_MODEL_NAME);
        return device;
    }
}
