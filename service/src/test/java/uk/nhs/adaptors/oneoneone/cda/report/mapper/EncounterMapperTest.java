package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;
import uk.nhs.connect.iucds.cda.ucr.TS;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EncounterMapperTest {

    @Mock
    private ParticipantMapper participantMapper;

    @Mock
    private PeriodMapper periodMapper;

    @InjectMocks
    private EncounterMapper encounterMapper;

    @Mock
    private Period period;

    @Mock
    private Encounter.EncounterParticipantComponent encounterParticipantComponent;

    @Test
    public void mapEncounter(){
        POCDMT000002UK01ClinicalDocument1 clinicalDocument = mock(POCDMT000002UK01ClinicalDocument1.class);
        POCDMT000002UK01Participant1 participant = mock(POCDMT000002UK01Participant1.class);
        TS effectiveTime = mock(TS.class);
        when(clinicalDocument.getParticipantArray()).thenReturn(new POCDMT000002UK01Participant1[] {participant});
        when(clinicalDocument.getEffectiveTime()).thenReturn(effectiveTime);

        when(periodMapper.mapPeriod(ArgumentMatchers.isA(TS.class))).thenReturn(period);
        when(participantMapper.mapEncounterParticipant(ArgumentMatchers.any())).thenReturn(encounterParticipantComponent);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);
        assertThat(encounter.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getPeriod()).isEqualTo(period);
        assertThat(encounter.getParticipantFirstRep()).isEqualTo(encounterParticipantComponent);

    }

}
