package uk.nhs.adaptors.oneoneone.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

import org.junit.jupiter.api.Test;

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
}
