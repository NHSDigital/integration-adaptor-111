package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncounterParticipant;
import uk.nhs.connect.iucds.cda.ucr.XEncounterParticipant;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ParticipantMapperTest {

    @Mock
    PractitionerMapper practitionerMapper;

    @Mock
    PeriodMapper periodMapper;

    @InjectMocks
    ParticipantMapper participantMapper;

    @Mock
    Practitioner practitioner;

    @Mock
    Period period;

    private POCDMT000002UK01EncounterParticipant encounterParticipant;

    @Test
    public void mapParticipant() {
        encounterParticipant = POCDMT000002UK01EncounterParticipant.Factory.newInstance();
        encounterParticipant.setTypeCode(XEncounterParticipant.Enum.forString("CON"));

        when(periodMapper.mapPeriod(ArgumentMatchers.any()))
            .thenReturn(period);

        when(practitionerMapper.mapPractitioner(ArgumentMatchers.any()))
            .thenReturn(practitioner);

        Encounter.EncounterParticipantComponent participantComponent = participantMapper.mapEncounterParticipant(encounterParticipant);

        assertThat(participantComponent.getIndividual().getResource()).isEqualTo(practitioner);
        assertThat(participantComponent.getPeriod()).isEqualTo(period);
        assertThat(participantComponent.getType().get(0).getText()).isEqualTo("CON");
    }

}
