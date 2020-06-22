package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import uk.nhs.connect.iucds.cda.ucr.AD;

import org.apache.xmlbeans.XmlString;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddressMapperTest {

    @Mock
    private PeriodMapper periodMapper;

    @InjectMocks
    private AddressMapper addressMapper;

    @Mock
    private Period period;

    private static final String CITY = "Small City";
    private static final String ADDRESS_LINE = "Magnolia Crescent 1";
    private static final String POSTAL_CODE = "BS2 RF2";
    private static final String COUNTRY = "United Kingdom";
    private static final String DESCRIPTION = "test description";
    private static final String STATE = "Small County";
    private static final String DISTRICT = "Little District";

    @Test
    public void mapAddress() {
        AD ad = AD.Factory.newInstance();
        ad.addNewStreetAddressLine().set(XmlString.Factory.newValue(ADDRESS_LINE));
        ad.addNewCity().set(XmlString.Factory.newValue(CITY));
        ad.addNewPostalCode().set(XmlString.Factory.newValue(POSTAL_CODE));
        ad.addNewCountry().set(XmlString.Factory.newValue(COUNTRY));
        ad.addNewDesc().set(XmlString.Factory.newValue(DESCRIPTION));
        ad.addNewState().set(XmlString.Factory.newValue(STATE));
        ad.addNewPrecinct().set(XmlString.Factory.newValue(DISTRICT));
        ad.addNewUseablePeriod();

        when(periodMapper.mapPeriod(ArgumentMatchers.any()))
            .thenReturn(period);

        Address address = addressMapper.mapAddress(ad);

        assertThat(address.getLine().get(0).getValue()).isEqualTo(ADDRESS_LINE);
        assertThat(address.getCity()).isEqualTo(CITY);
        assertThat(address.getPostalCode()).isEqualTo(POSTAL_CODE);
        assertThat(address.getCountry()).isEqualTo(COUNTRY);
        assertThat(address.getText()).isEqualTo(DESCRIPTION);
        assertThat(address.getState()).isEqualTo(STATE);
        assertThat(address.getDistrict()).isEqualTo(DISTRICT);
        assertThat(address.getPeriod()).isEqualTo(period);
    }


}