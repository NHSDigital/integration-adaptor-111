package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.apache.xmlbeans.XmlString;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddressMapperTest {

    private static final String CITY = "Small City";
    private static final List<String> ADDRESS_USE_LIST = Arrays.asList("H");
    private static final List<String> ADDRESS_TYPE_LIST = Arrays.asList("PHYS");
    private static final String ADDRESS_LINE = "Magnolia Crescent 1";
    private static final String POSTAL_CODE = "BS2 RF2";
    private static final String COUNTRY = "United Kingdom";
    private static final String DESCRIPTION = "test description";
    private static final String STATE = "Small County";
    private static final String DISTRICT = "Little District";
    @Mock
    private PeriodMapper periodMapper;
    @InjectMocks
    private AddressMapper addressMapper;
    @Mock
    private Period period;
    @Mock
    private NodeUtil nodeUtil;
    @Mock
    private AD itkAddress;

    @BeforeEach
    public void setup() {
        AD ad = AD.Factory.newInstance();
        ad.addNewStreetAddressLine().set(XmlString.Factory.newValue(ADDRESS_LINE));
        ad.addNewCity().set(XmlString.Factory.newValue(CITY));
        ad.addNewPostalCode().set(XmlString.Factory.newValue(POSTAL_CODE));
        ad.addNewCountry().set(XmlString.Factory.newValue(COUNTRY));
        ad.addNewDesc().set(XmlString.Factory.newValue(DESCRIPTION));
        ad.addNewState().set(XmlString.Factory.newValue(STATE));
        ad.addNewPrecinct().set(XmlString.Factory.newValue(DISTRICT));
        ad.addNewUseablePeriod();

        when(itkAddress.sizeOfUseablePeriodArray()).thenReturn(1);
        when(periodMapper.mapPeriod(ArgumentMatchers.any()))
            .thenReturn(period);
        when(itkAddress.getStreetAddressLineArray()).thenReturn(ad.getStreetAddressLineArray());
        when(nodeUtil.getNodeValueString(ad.getStreetAddressLineArray(0))).thenReturn(ADDRESS_LINE);
        when(itkAddress.isSetUse()).thenReturn(true);
        when(itkAddress.getUse()).thenReturn(ADDRESS_USE_LIST);
        when(itkAddress.sizeOfPostalCodeArray()).thenReturn(1);
        when(itkAddress.getPostalCodeArray(0)).thenReturn(ad.getPostalCodeArray(0));
        when(nodeUtil.getNodeValueString(ad.getPostalCodeArray(0))).thenReturn(POSTAL_CODE);
        when(itkAddress.sizeOfCityArray()).thenReturn(1);
        when(itkAddress.getCityArray(0)).thenReturn(ad.getCityArray(0));
        when(nodeUtil.getNodeValueString(ad.getCityArray(0))).thenReturn(CITY);
        when(itkAddress.sizeOfDescArray()).thenReturn(1);
        when(itkAddress.getDescArray(0)).thenReturn(ad.getDescArray(0));
        when(nodeUtil.getNodeValueString(ad.getDescArray(0))).thenReturn(DESCRIPTION);
        when(itkAddress.sizeOfCountryArray()).thenReturn(1);
        when(itkAddress.getCountryArray(0)).thenReturn(ad.getCountryArray(0));
        when(nodeUtil.getNodeValueString(ad.getCountryArray(0))).thenReturn(COUNTRY);
        when(itkAddress.sizeOfStateArray()).thenReturn(1);
        when(itkAddress.getStateArray(0)).thenReturn(ad.getStateArray(0));
        when(nodeUtil.getNodeValueString(ad.getStateArray(0))).thenReturn(STATE);
        when(itkAddress.getPrecinctArray(0)).thenReturn(ad.getPrecinctArray(0));
        when(nodeUtil.getNodeValueString(ad.getPrecinctArray(0))).thenReturn(DISTRICT);
    }

    @Test
    public void shouldMapAddress() {
        Address address = addressMapper.mapAddress(itkAddress);

        assertThat(address.getUse()).isEqualTo(AddressUse.HOME);
        assertThat(address.getLine().get(0).getValue()).isEqualTo(ADDRESS_LINE);
        assertThat(address.getCity()).isEqualTo(CITY);
        assertThat(address.getPostalCode()).isEqualTo(POSTAL_CODE);
        assertThat(address.getCountry()).isEqualTo(COUNTRY);
        assertThat(address.getText()).isEqualTo(DESCRIPTION);
        assertThat(address.getState()).isEqualTo(STATE);
        assertThat(address.getDistrict()).isEqualTo(DISTRICT);
        assertThat(address.getPeriod()).isEqualTo(period);
    }

    @Test
    public void shouldMapAddressType() {
        when(itkAddress.getUse()).thenReturn(ADDRESS_TYPE_LIST);
        Address address = addressMapper.mapAddress(itkAddress);

        assertThat(address.getType()).isEqualTo(AddressType.PHYSICAL);
    }
}