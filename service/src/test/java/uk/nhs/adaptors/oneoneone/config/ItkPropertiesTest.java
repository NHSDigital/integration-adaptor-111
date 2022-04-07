package uk.nhs.adaptors.oneoneone.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ItkPropertiesTest {

    private ItkProperties itkProperties = new ItkProperties();

    @Test
    public void shouldSplitAndTrimOdsCodes() {
        String odsCodes = ",5L399, AB745,,FK043,";
        itkProperties.setOdsCodes(odsCodes);

        assertThat(itkProperties.getOdsCodes()).isEqualTo(newArrayList("5L399", "AB745", "FK043"));
    }

    @Test
    public void shouldSplitAndTrimDosIds() {
        String dosIds = ",2000072936, 5500073336,,1030072239,";
        itkProperties.setDosIds(dosIds);

        assertThat(itkProperties.getDosIds()).isEqualTo(newArrayList("2000072936", "5500073336", "1030072239"));
    }

    @Test
    public void shouldSetOdsCodeDosIds() throws IOException {
        OdsCodesDosIds odsCodesDosIds = new ObjectMapper().readValue(this.getClass().getResource("/configuration/odsCodesDosIds.json"),
            OdsCodesDosIds.class);

        itkProperties.setOdsCodesDosIds(odsCodesDosIds);

        assertThat(itkProperties.getDosIds()).isEqualTo(newArrayList("26428"));
        assertThat(itkProperties.getOdsCodes()).isEqualTo(newArrayList("EM396", "5L399"));
    }
}
