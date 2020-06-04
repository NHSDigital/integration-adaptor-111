package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.mockito.Mockito.when;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;

import org.hl7.fhir.dstu3.model.Encounter;
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
public class EncounterMapperTest {

    @Mock
    ParticipantMapper participantMapper;

    @Mock
    PeriodMapper periodMapper;

    @InjectMocks
    EncounterMapper encounterMapper;

    @Mock
    Period period;

    @Mock
    Encounter.EncounterParticipantComponent encounterParticipantComponent;


    @Test
    public void mapEncounter(){
        POCDMT000002UK01EncompassingEncounter encompassingEncounter = POCDMT000002UK01EncompassingEncounter.Factory.newInstance();
        encompassingEncounter.addNewEncounterParticipant();

        when(periodMapper.mapPeriod(ArgumentMatchers.any())).thenReturn(period);
        when(participantMapper.mapEncounterParticipant(ArgumentMatchers.any())).thenReturn(encounterParticipantComponent);

        Encounter encounter = encounterMapper.mapEncounter(encompassingEncounter);
        assertThat(encounter.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getPeriod()).isEqualTo(period);
        assertThat(encounter.getParticipantFirstRep()).isEqualTo(encounterParticipantComponent);

    }

}
