package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.apache.xmlbeans.XmlString;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.PN;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.HumanName.NameUse.OFFICIAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HumanNameMapperTest {

    private static final String GIVEN = "John";
    private static final String PREFIX = "sir";
    private static final String SUFFIX = "de Windermere";
    private static final String FAMILY = "Lloyd";
    @Mock
    private PeriodMapper periodMapper;
    @InjectMocks
    private HumanNameMapper humanNameMapper;
    @Mock
    private Period period;
    @Mock
    private NodeUtil nodeUtil;
    @Mock
    private PN itkPersonName;

    @Test
    public void shouldMapHumanName() {
        PN pn = PN.Factory.newInstance();
        pn.addNewGiven().set(XmlString.Factory.newValue(GIVEN));
        pn.addNewPrefix().set(XmlString.Factory.newValue(PREFIX));
        pn.addNewSuffix().set(XmlString.Factory.newValue(SUFFIX));
        pn.addNewFamily().set(XmlString.Factory.newValue(FAMILY));
        pn.addNewValidTime();

        when(periodMapper.mapPeriod(ArgumentMatchers.any()))
            .thenReturn(period);

        when(nodeUtil.hasSubNodes(any())).thenReturn(true);
        when(itkPersonName.getGivenArray()).thenReturn(pn.getGivenArray());
        when(nodeUtil.getNodeValueString(pn.getGivenArray(0))).thenReturn(GIVEN);
        when(itkPersonName.getPrefixArray()).thenReturn(pn.getPrefixArray());
        when(nodeUtil.getNodeValueString(pn.getPrefixArray(0))).thenReturn(PREFIX);
        when(itkPersonName.getSuffixArray()).thenReturn(pn.getSuffixArray());
        when(nodeUtil.getNodeValueString(pn.getSuffixArray(0))).thenReturn(SUFFIX);
        when(itkPersonName.sizeOfFamilyArray()).thenReturn(1);
        when(itkPersonName.getFamilyArray(0)).thenReturn(pn.getFamilyArray(0));
        when(nodeUtil.getNodeValueString(pn.getFamilyArray(0))).thenReturn(FAMILY);
        when(itkPersonName.isSetValidTime()).thenReturn(true);

        HumanName humanName = humanNameMapper.mapHumanName(itkPersonName);

        assertThat(humanName.getGivenAsSingleString()).isEqualTo(GIVEN);
        assertThat(humanName.getPrefixAsSingleString()).isEqualTo(PREFIX);
        assertThat(humanName.getSuffixAsSingleString()).isEqualTo(SUFFIX);
        assertThat(humanName.getFamily()).isEqualTo(FAMILY);
        assertThat(humanName.getPeriod()).isEqualTo(period);
        assertThat(humanName.getUse()).isEqualTo(OFFICIAL);
    }
}
