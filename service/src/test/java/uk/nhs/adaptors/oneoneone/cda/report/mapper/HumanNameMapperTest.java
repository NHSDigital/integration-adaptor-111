package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import uk.nhs.connect.iucds.cda.ucr.PN;

import org.apache.xmlbeans.XmlString;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class HumanNameMapperTest {

    @Mock
    PeriodMapper periodMapper;

    @InjectMocks
    HumanNameMapper humanNameMapper;

    @Mock
    Period period;
    public static final String GIVEN = "John";
    public static final String PREFIX = "sir";
    public static final String SUFFIX = "de Windermere";
    public static final String FAMILY = "Lloyd";

    @Test
    public void mapHumanName() {
        PN pn = PN.Factory.newInstance();
        pn.addNewGiven().set(XmlString.Factory.newValue(GIVEN));
        pn.addNewPrefix().set(XmlString.Factory.newValue(PREFIX));
        pn.addNewSuffix().set(XmlString.Factory.newValue(SUFFIX));
        pn.addNewFamily().set(XmlString.Factory.newValue(FAMILY));
        pn.addNewValidTime();

        when(periodMapper.mapPeriod(ArgumentMatchers.any()))
            .thenReturn(period);

        HumanName humanName = humanNameMapper.mapHumanName(pn);

        assertThat(humanName.getGivenAsSingleString()).isEqualTo(GIVEN);
        assertThat(humanName.getPrefixAsSingleString()).isEqualTo(PREFIX);
        assertThat(humanName.getSuffixAsSingleString()).isEqualTo(SUFFIX);
        assertThat(humanName.getFamily()).isEqualTo(FAMILY);
        assertThat(humanName.getPeriod()).isEqualTo(period);
    }
}
