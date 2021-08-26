package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.util.Optional.empty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.emptyList;
import static org.assertj.core.util.Lists.newArrayList;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.enums.MessageHeaderEvent.DISCHARGE_DETAILS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.service.AppointmentService;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.ED;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01DataEnterer;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RecordTarget;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.iucds.cda.ucr.TS;

@ExtendWith(MockitoExtension.class)
public class EncounterMapperTest {

    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";
    private static final Coding AUTHOR_CODING = new Coding("http://hl7.org/fhir/ValueSet/encounter-participant-type", "PPRF",
        "Author");
    private static final Coding RESPONSIBLE_PARTY_CODING = new Coding("http://hl7.org/fhir/ValueSet/encounter-participant-type", "ATND",
        "Responsible Party");
    private static final String ID_ROOT = "2.16.840.1.113883.2.1.3.2.4.18.34";
    private static final String ID_EXTENSION = "CASEREF1234";
    private static final String TEXT_REPRESENTATION = "111 Encounter Copy for Information";
    private static final int OTHER_PARTICIPANTS_SIZE = 4;
    @Mock
    private ParticipantMapper participantMapper;
    @Mock
    private PeriodMapper periodMapper;
    @Mock
    private InformantMapper informantMapper;
    @Mock
    private DataEntererMapper dataEntererMapper;
    @Mock
    private ServiceProviderMapper serviceProviderMapper;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private AppointmentService appointmentService;
    @InjectMocks
    private EncounterMapper encounterMapper;
    @Mock
    private Period period;
    @Mock
    private Encounter.EncounterParticipantComponent encounterParticipantComponent;
    @Mock
    private Organization serviceProvider;
    @Mock
    private Appointment appointment;
    @Mock
    private Patient patient;
    @Mock
    private Encounter.EncounterLocationComponent locationComponent;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private POCDMT000002UK01Component3 component3;
    @Mock
    private POCDMT000002UK01Entry entry;
    @Mock
    private POCDMT000002UK01StructuredBody structuredBody;
    @Mock
    private POCDMT000002UK01Section section01;
    @Mock
    private POCDMT000002UK01Encounter encounter;
    @Mock
    private POCDMT000002UK01EncompassingEncounter encompassingEncounter;
    @Mock
    private POCDMT000002UK01Component1 component1;
    @Mock
    private POCDMT000002UK01Component2 component2;
    @Mock
    private ED encounterTextED;
    @Mock
    private NodeUtil nodeUtil;
    @Mock
    private ResourceUtil resourceUtil;
    @Mock
    private II identifier;
    @Mock
    POCDMT000002UK01Participant1 participant2;
    @Mock
    POCDMT000002UK01Participant1 participant;

    @BeforeEach
    public void setUp() {
        POCDMT000002UK01RecordTarget recordTarget = mock(POCDMT000002UK01RecordTarget.class);
        POCDMT000002UK01PatientRole patientRole = mock(POCDMT000002UK01PatientRole.class);
        POCDMT000002UK01Organization organization = mock(POCDMT000002UK01Organization.class);

        when(clinicalDocument.getRecordTargetArray()).thenReturn(new POCDMT000002UK01RecordTarget[] {recordTarget});
        when(clinicalDocument.getRecordTargetArray(0)).thenReturn(recordTarget);
        when(clinicalDocument.sizeOfRecordTargetArray()).thenReturn(1);
        when(recordTarget.getPatientRole()).thenReturn(patientRole);
        when(patientRole.getProviderOrganization()).thenReturn(organization);

        mockClinicalDocument(clinicalDocument);
        mockEncompassingEncounter();
        mockParticipant(clinicalDocument);
        mockLocation();
        mockPeriod(clinicalDocument);
        mockServiceProvider();
        mockAppointment();
        mockSubject();
        mockParticipantWithAuthorInformantAndDataEnterer(clinicalDocument);
        mockEncounterTypeAndReason();
    }

    private void mockEncompassingEncounter() {
        when(component1.getEncompassingEncounter()).thenReturn(encompassingEncounter);
        when(identifier.getRoot()).thenReturn(ID_ROOT);
        when(identifier.getExtension()).thenReturn(ID_EXTENSION);
        when(encompassingEncounter.getIdArray()).thenReturn(new II[] {identifier});
    }

    private void mockClinicalDocument(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        POCDMT000002UK01RecordTarget mockRecordTarget = mock(POCDMT000002UK01RecordTarget.class);
        when(clinicalDocument.getRecordTargetArray(anyInt())).thenReturn(mockRecordTarget);
        when(mockRecordTarget.getPatientRole()).thenReturn(mock(POCDMT000002UK01PatientRole.class));
        when(clinicalDocument.getComponentOf()).thenReturn(component1);
    }

    private void mockParticipant(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        when(clinicalDocument.getParticipantArray()).thenReturn(new POCDMT000002UK01Participant1[] {participant, participant2});
        when(participant.getTypeCode()).thenReturn("CALLBCK");
        when(participant2.getTypeCode()).thenReturn("REFT");
        when(participantMapper.mapEncounterParticipant(any())).thenReturn(encounterParticipantComponent);
    }

    private void mockLocation() {
        when(locationMapper.mapOrganizationToLocationComponent(any())).thenReturn(locationComponent);
    }

    private void mockPeriod(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        TS effectiveTime = mock(TS.class);

        when(clinicalDocument.getEffectiveTime()).thenReturn(effectiveTime);
        when(periodMapper.mapPeriod(ArgumentMatchers.isA(TS.class))).thenReturn(period);
    }

    private void mockServiceProvider() {
        when(serviceProviderMapper.mapServiceProvider(any())).thenReturn(serviceProvider);
    }

    private void mockAppointment() {
        when(appointmentService.retrieveAppointment(any(), any())).thenReturn(Optional.of(appointment));
    }

    private void mockSubject() {
        when(patientMapper.mapPatient(any())).thenReturn(patient);
    }

    private void mockParticipantWithAuthorInformantAndDataEnterer(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        mockParticipant(clinicalDocument);

        POCDMT000002UK01Informant12 informant = mock(POCDMT000002UK01Informant12.class);
        POCDMT000002UK01DataEnterer dataEnterer = mock(POCDMT000002UK01DataEnterer.class);

        when(clinicalDocument.sizeOfInformantArray()).thenReturn(1);
        when(clinicalDocument.getInformantArray()).thenReturn(new POCDMT000002UK01Informant12[] {informant});
        when(clinicalDocument.isSetDataEnterer()).thenReturn(true);
        when(clinicalDocument.getDataEnterer()).thenReturn(dataEnterer);

        when(informantMapper.mapInformantIntoParticipantComponent(any())).thenReturn(Optional.of(encounterParticipantComponent));
        when(dataEntererMapper.mapDataEntererIntoParticipantComponent(any())).thenReturn(encounterParticipantComponent);
        when(participantMapper.mapEncounterRelatedPerson(any(), any())).thenReturn(encounterParticipantComponent);
    }

    private void mockEncounterTypeAndReason() {
        String encounterText = "Encounter text";

        POCDMT000002UK01Component3[] componentArray = new POCDMT000002UK01Component3[1];
        componentArray[0] = component3;
        POCDMT000002UK01Entry[] entryArray = new POCDMT000002UK01Entry[1];
        entryArray[0] = entry;

        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(component2.isSetStructuredBody()).thenReturn(true);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        when(structuredBody.getComponentArray()).thenReturn(componentArray);
        when(component3.getSection()).thenReturn(section01);
        when(section01.getEntryArray()).thenReturn(entryArray);
        when(entry.getEncounter()).thenReturn(encounter);
        when(entry.isSetEncounter()).thenReturn(true);
        when(encounter.isSetText()).thenReturn(true);
        when(encounter.getText()).thenReturn(encounterTextED);
        when(nodeUtil.getNodeValueString(any())).thenReturn(encounterText);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
    }

    @Test
    public void shouldMapEncounter() {
        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, new ArrayList<>(), empty(), DISCHARGE_DETAILS.toCoding());

        verifyEncounter(encounter);
    }

    private void verifyEncounter(Encounter encounter) {
        String encounterDivText = "<div xmlns=\"http://www.w3.org/1999/xhtml\">Encounter text</div>";
        assertThat(encounter.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getPeriod()).isEqualTo(period);
        assertThat(encounter.getParticipantFirstRep()).isEqualTo(encounterParticipantComponent);
        assertThat(encounter.getServiceProviderTarget()).isEqualTo(serviceProvider);
        assertThat(encounter.getLocationFirstRep()).isEqualTo(locationComponent);
        assertThat(encounter.getSubjectTarget()).isEqualTo(patient);
        assertThat(encounter.getText().getDiv().toString()).isEqualTo(encounterDivText);
        assertThat(encounter.getIdentifierFirstRep().getValue()).isEqualTo(ID_EXTENSION);
        assertThat(encounter.getIdentifierFirstRep().getSystem()).isEqualTo(ID_ROOT);
        assertThat(encounter.getTypeFirstRep().getText()).isEqualTo(TEXT_REPRESENTATION);
    }

    @Test
    public void mapEncounterTest() {
        mockParticipant(clinicalDocument);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, emptyList(), empty(), DISCHARGE_DETAILS.toCoding());
        verifyEncounter(encounter);
        assertThat(encounter.getLocation().size()).isEqualTo(1);
    }

    @Test
    public void mapEncounterTest1() {
        mockParticipant(clinicalDocument);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, emptyList(), empty(), DISCHARGE_DETAILS.toCoding());
        assertThat(encounter.getParticipant().size()).isEqualTo(4);
        verify(participantMapper, times(1)).mapEncounterParticipant(participant);
        verifyNoMoreInteractions(participantMapper);
    }

    @Test
    public void mapEncounterWithMultipleParticipantTypes() {
        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, newArrayList(new PractitionerRole()),
            Optional.of(new PractitionerRole()), DISCHARGE_DETAILS.toCoding());
        verifyEncounter(encounter);
        verifyParticipants(encounter);
    }

    private void verifyParticipants(Encounter encounter) {
        long authorsCount = getParticipantsCountByCoding(encounter, AUTHOR_CODING);
        assertThat(authorsCount).isEqualTo(1);

        long responsiblePartyCount = getParticipantsCountByCoding(encounter, RESPONSIBLE_PARTY_CODING);
        assertThat(responsiblePartyCount).isEqualTo(1);

        long otherParticipantsCount = encounter.getParticipant().stream()
            .filter(it -> it.equals(encounterParticipantComponent))
            .count();
        assertThat(otherParticipantsCount).isEqualTo(OTHER_PARTICIPANTS_SIZE);
    }

    private long getParticipantsCountByCoding(Encounter encounter, Coding coding) {
        return encounter.getParticipant().stream()
            .filter(it -> it.getTypeFirstRep() != null)
            .filter(it -> it.getTypeFirstRep().getCodingFirstRep().equalsDeep(coding))
            .count();
    }

    @Test
    public void mapEncounterWhenHealthcareLocationIsNotNull() {
        when(locationMapper.mapHealthcareFacilityToLocationComponent(clinicalDocument)).thenReturn(locationComponent);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, emptyList(), empty(), DISCHARGE_DETAILS.toCoding());

        verifyEncounter(encounter);
        assertThat(encounter.getLocation().size()).isEqualTo(2);
    }
}
