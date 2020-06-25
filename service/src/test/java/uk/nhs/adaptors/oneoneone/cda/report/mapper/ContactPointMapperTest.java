package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.TEL;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContactPointMapperTest {

    private static final String TEL_NUMBER = "1357962783";
    private static final String USE_ITK = "H";
    @Mock
    private PeriodMapper periodMapper;
    @InjectMocks
    private ContactPointMapper contactPointMapper;
    @Mock
    private Period period;

    @Test
    public void shouldMapContactPoint() {
        TEL tel = TEL.Factory.newInstance();
        tel.setValue(TEL_NUMBER);
        tel.setUse(Collections.singletonList(USE_ITK));
        tel.addNewUseablePeriod();

        when(periodMapper.mapPeriod(ArgumentMatchers.any())).thenReturn(period);

        ContactPoint contactPoint = contactPointMapper.mapContactPoint(tel);

        assertThat(contactPoint.getValue()).isEqualTo(TEL_NUMBER);
        assertThat(contactPoint.getUse()).isEqualTo(ContactPoint.ContactPointUse.HOME);
        assertThat(contactPoint.getPeriod()).isEqualTo(period);
        assertThat(contactPoint.getSystem()).isEqualTo(ContactPoint.ContactPointSystem.OTHER);
    }

}
