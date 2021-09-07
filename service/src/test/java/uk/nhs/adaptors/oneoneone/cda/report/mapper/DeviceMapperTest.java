package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.adaptors.oneoneone.config.OneOneOneProperties;
@ExtendWith(MockitoExtension.class)
public class DeviceMapperTest {
    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";
    private static final String VERSION = "0.8.0";
    private static final String MODEL = "111 Adaptor";

    @InjectMocks
    private DeviceMapper deviceMapper;

    @Mock
    private ResourceUtil resourceUtil;
    @Mock
    private OneOneOneProperties oneOneOneProperties;

    @BeforeEach
    public void setUp() {
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
        when(oneOneOneProperties.getVersion()).thenReturn(VERSION);
    }
    @Test
    public void shouldMapDevice() {
        Device device = deviceMapper.mapDevice();

        assertThat(device.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(device.getVersion()).isEqualTo(VERSION);
        assertThat(device.getModel()).isEqualTo(MODEL);
    }


}
