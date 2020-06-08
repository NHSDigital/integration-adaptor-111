package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import uk.nhs.adaptors.oneoneone.cda.report.service.AppointmentService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01DataEnterer;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;
import uk.nhs.connect.iucds.cda.ucr.TS;

import org.hl7.fhir.dstu3.model.Appointment;
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

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private InformantMapper informantMapper;

    @Mock
    private DataEntererMapper dataEntererMapper;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private EncounterMapper encounterMapper;

    @Mock
    private Period period;

    @Mock
    private Encounter.EncounterParticipantComponent encounterParticipantComponent;

    @Mock
    private Appointment appointment;

    @Test
    public void mapEncounter(){
        POCDMT000002UK01ClinicalDocument1 clinicalDocument = mock(POCDMT000002UK01ClinicalDocument1.class);
        POCDMT000002UK01Participant1 participant = mock(POCDMT000002UK01Participant1.class);
        TS effectiveTime = mock(TS.class);
        when(clinicalDocument.getParticipantArray()).thenReturn(new POCDMT000002UK01Participant1[] {participant});
        when(clinicalDocument.getEffectiveTime()).thenReturn(effectiveTime);

        when(periodMapper.mapPeriod(ArgumentMatchers.isA(TS.class))).thenReturn(period);
        when(participantMapper.mapEncounterParticipant(any())).thenReturn(encounterParticipantComponent);
        when(appointmentService.retrieveAppointment(any(),any(),any())).thenReturn(Optional.of(appointment));

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);
        assertThat(encounter.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getPeriod()).isEqualTo(period);
        assertThat(encounter.getParticipantFirstRep()).isEqualTo(encounterParticipantComponent);
        assertThat(encounter.getAppointmentTarget()).isEqualTo(appointment);
    }

    @Test
    public void mapEncounterWhenAuthorInformantAndDataEntereArePresent(){
        POCDMT000002UK01ClinicalDocument1 clinicalDocument = mock(POCDMT000002UK01ClinicalDocument1.class);
        POCDMT000002UK01Participant1 participant = mock(POCDMT000002UK01Participant1.class);
        POCDMT000002UK01Author author = mock(POCDMT000002UK01Author.class);
        POCDMT000002UK01Informant12 informant = mock(POCDMT000002UK01Informant12.class);
        POCDMT000002UK01DataEnterer dataEnterer = mock(POCDMT000002UK01DataEnterer.class);
        TS effectiveTime = mock(TS.class);

        when(clinicalDocument.getParticipantArray()).thenReturn(new POCDMT000002UK01Participant1[] {participant});
        when(clinicalDocument.sizeOfAuthorArray()).thenReturn(1);
        when(clinicalDocument.getAuthorArray()).thenReturn(new POCDMT000002UK01Author[] {author});
        when(clinicalDocument.sizeOfInformantArray()).thenReturn(1);
        when(clinicalDocument.getInformantArray()).thenReturn(new POCDMT000002UK01Informant12[] {informant});
        when(clinicalDocument.isSetDataEnterer()).thenReturn(true);
        when(clinicalDocument.getDataEnterer()).thenReturn(dataEnterer);
        when(clinicalDocument.getEffectiveTime()).thenReturn(effectiveTime);

        when(periodMapper.mapPeriod(ArgumentMatchers.isA(TS.class))).thenReturn(period);
        when(participantMapper.mapEncounterParticipant(any())).thenReturn(encounterParticipantComponent);
        when(authorMapper.mapAuthorIntoParticipantComponent(any())).thenReturn(encounterParticipantComponent);
        when(informantMapper.mapInformantIntoParticipantComponent(any())).thenReturn(Optional.of(encounterParticipantComponent));
        when(dataEntererMapper.mapDataEntererIntoParticipantComponent(any())).thenReturn(encounterParticipantComponent);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);
        assertThat(encounter.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getPeriod()).isEqualTo(period);
        assertThat(encounter.getParticipant().size()).isEqualTo(4);
        for (Encounter.EncounterParticipantComponent component : encounter.getParticipant()) {
            assertThat(component).isEqualTo(encounterParticipantComponent);
        }
    }

}
