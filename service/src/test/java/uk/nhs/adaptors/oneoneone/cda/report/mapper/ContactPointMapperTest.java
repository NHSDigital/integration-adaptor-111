package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.EMAIL;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.PHONE;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse.HOME;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse.MOBILE;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.connect.iucds.cda.ucr.TEL;

@ExtendWith(MockitoExtension.class)
public class ContactPointMapperTest {

    private static final String TEL_NUMBER = "tel:1357962783";
    private static final String EMAIL_ADDRESS = "mailto:test111@nhs.uk";
    private static final String USE_ITK_HOME = "H";
    private static final String USE_ITK_MOBILE = "MC";

    @Mock
    private PeriodMapper periodMapper;
    @InjectMocks
    private ContactPointMapper contactPointMapper;
    @Mock
    private Period period;

    @Test
    public void shouldMapContactPointPhone() {
        TEL tel = TEL.Factory.newInstance();
        tel.setValue(TEL_NUMBER);
        tel.setUse(singletonList(USE_ITK_HOME));
        tel.addNewUseablePeriod();

        when(periodMapper.mapPeriod(ArgumentMatchers.any())).thenReturn(period);

        ContactPoint contactPoint = contactPointMapper.mapContactPoint(tel);

        assertThat(contactPoint.getValue()).isEqualTo(TEL_NUMBER);
        assertThat(contactPoint.getUse()).isEqualTo(HOME);
        assertThat(contactPoint.getPeriod()).isEqualTo(period);
        assertThat(contactPoint.getSystem()).isEqualTo(PHONE);
    }

    @Test
    public void shouldMapContactPointEmail() {
        TEL tel = TEL.Factory.newInstance();
        tel.setValue(EMAIL_ADDRESS);
        tel.setUse(singletonList(USE_ITK_MOBILE));
        tel.addNewUseablePeriod();

        when(periodMapper.mapPeriod(ArgumentMatchers.any())).thenReturn(period);

        ContactPoint contactPoint = contactPointMapper.mapContactPoint(tel);

        assertThat(contactPoint.getValue()).isEqualTo(EMAIL_ADDRESS);
        assertThat(contactPoint.getUse()).isEqualTo(MOBILE);
        assertThat(contactPoint.getPeriod()).isEqualTo(period);
        assertThat(contactPoint.getSystem()).isEqualTo(EMAIL);
    }
}
