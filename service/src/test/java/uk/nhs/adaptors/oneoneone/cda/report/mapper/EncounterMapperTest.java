package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.codesystems.EncounterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.service.AppointmentService;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.CDNPfITCDAUrl;
import uk.nhs.connect.iucds.cda.ucr.ED;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01DataEnterer;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01InfrastructureRootTypeId;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RecordTarget;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.iucds.cda.ucr.TS;

@ExtendWith(MockitoExtension.class)
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
    private EpisodeOfCare episodeOfCare;
    @Mock
    private ReferralRequest referralRequest;
    @Mock
    private Encounter.EncounterLocationComponent locationComponent;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private EpisodeOfCareMapper episodeOfCareMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private ReferralRequestMapper referralRequestMapper;
    @Mock
    private List<HealthcareService> healthcareServiceList;
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
    private POCDMT000002UK01Component2 component2;
    @Mock
    private POCDMT000002UK01InfrastructureRootTypeId typeId;
    @Mock
    private CDNPfITCDAUrl cdnPfITCDAUrl;
    @Mock
    private ED encounterTextED;
    @Mock
    private NodeUtil nodeUtil;

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
        mockParticipant(clinicalDocument);
        mockLocation();
        mockPeriod(clinicalDocument);
        mockServiceProvider();
        mockAppointment();
        mockSubject();
        mockEpisodeOfCare();
        mockReferralRequest();
        mockParticipantWithAuthorInformantAndDataEnterer(clinicalDocument);
        mockEncounterTypeAndReason();
    }

    private void mockClinicalDocument(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        POCDMT000002UK01RecordTarget mockRecordTarget = mock(POCDMT000002UK01RecordTarget.class);
        when(clinicalDocument.getRecordTargetArray(anyInt())).thenReturn(mockRecordTarget);
        when(mockRecordTarget.getPatientRole()).thenReturn(mock(POCDMT000002UK01PatientRole.class));
    }

    private void mockParticipant(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        POCDMT000002UK01Participant1 participant = mock(POCDMT000002UK01Participant1.class);

        when(clinicalDocument.getParticipantArray()).thenReturn(new POCDMT000002UK01Participant1[] {participant});
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
        when(appointmentService.retrieveAppointment(any(), any(), any())).thenReturn(Optional.of(appointment));
    }

    private void mockSubject() {
        when(patientMapper.mapPatient(any())).thenReturn(patient);
    }

    private void mockEpisodeOfCare() {
        when(episodeOfCareMapper.mapEpisodeOfCare(any(POCDMT000002UK01ClinicalDocument1.class), any(Reference.class)))
            .thenReturn(Optional.of(episodeOfCare));
    }

    private void mockReferralRequest() {
        when(referralRequestMapper.mapReferralRequest(any(), any()))
            .thenReturn(referralRequest);
    }

    private void mockParticipantWithAuthorInformantAndDataEnterer(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        mockParticipant(clinicalDocument);

        POCDMT000002UK01Author author = mock(POCDMT000002UK01Author.class);
        POCDMT000002UK01Informant12 informant = mock(POCDMT000002UK01Informant12.class);
        POCDMT000002UK01DataEnterer dataEnterer = mock(POCDMT000002UK01DataEnterer.class);

        when(clinicalDocument.sizeOfAuthorArray()).thenReturn(1);
        when(clinicalDocument.getAuthorArray()).thenReturn(new POCDMT000002UK01Author[] {author});
        when(clinicalDocument.sizeOfInformantArray()).thenReturn(1);
        when(clinicalDocument.getInformantArray()).thenReturn(new POCDMT000002UK01Informant12[] {informant});
        when(clinicalDocument.isSetDataEnterer()).thenReturn(true);
        when(clinicalDocument.getDataEnterer()).thenReturn(dataEnterer);

        when(authorMapper.mapAuthorIntoParticipantComponent(any())).thenReturn(encounterParticipantComponent);
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
        when(encounter.isSetTypeId()).thenReturn(true);
        when(encounter.getTypeId()).thenReturn(typeId);
        when(typeId.getAssigningAuthorityName()).thenReturn(EncounterType.ADMS.toString());
        when(entry.isSetEncounter()).thenReturn(true);
        when(encounter.isSetText()).thenReturn(true);
        when(encounter.getText()).thenReturn(encounterTextED);
        when(nodeUtil.getNodeValueString(any())).thenReturn(encounterText);
    }

    @Test
    public void shouldMapEncounter() {
        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, healthcareServiceList);
        verifyEncounter(encounter);
    }

    private void verifyEncounter(Encounter encounter) {
        String uuidBeginning = "urn:uuid:";
        String encounterDivText = "<div xmlns=\"http://www.w3.org/1999/xhtml\">Encounter text</div>";
        assertThat(encounter.getIdElement().getValue()).startsWith(uuidBeginning);
        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getPeriod()).isEqualTo(period);
        assertThat(encounter.getParticipantFirstRep()).isEqualTo(encounterParticipantComponent);
        assertThat(encounter.getAppointmentTarget()).isEqualTo(appointment);
        assertThat(encounter.getServiceProviderTarget()).isEqualTo(serviceProvider);
        assertThat(encounter.getLocationFirstRep()).isEqualTo(locationComponent);
        assertThat(encounter.getSubjectTarget()).isEqualTo(patient);
        assertThat(encounter.getEpisodeOfCareFirstRep().getResource()).isEqualTo(episodeOfCare);
        assertThat(encounter.getIncomingReferralFirstRep().getResource()).isEqualTo(referralRequest);
        assertThat(encounter.getTypeFirstRep().getText()).isEqualTo(EncounterType.ADMS.toString());
        assertThat(encounter.getText().getDiv().toString()).isEqualTo(encounterDivText);
    }

    @Test
    public void mapEncounterTest() {
        mockParticipant(clinicalDocument);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, healthcareServiceList);
        verifyEncounter(encounter);
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void mapEncounterWhenAuthorInformantAndDataEntererArePresent() {
        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument, healthcareServiceList);
        verifyEncounter(encounter);

        assertThat(encounter.getParticipant().size()).isEqualTo(5);
        for (Encounter.EncounterParticipantComponent component : encounter.getParticipant()) {
            assertThat(component).isEqualTo(encounterParticipantComponent);
        }
    }
}
