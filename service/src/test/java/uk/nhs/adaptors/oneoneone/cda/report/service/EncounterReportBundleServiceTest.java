package uk.nhs.adaptors.oneoneone.cda.report.service;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Consent;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.MessageHeader;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.mapper.CarePlanMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.CompositionMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ConsentMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.HealthcareServiceMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ListMapper;
import uk.nhs.adaptors.oneoneone.cda.report.util.PathwayUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Bundle.BundleType.MESSAGE;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EncounterReportBundleServiceTest {
    private static final Encounter ENCOUNTER;
    private static final IdType ENCOUNTER_ID = newRandomUuid();
    private static final Organization SERVICE_PROVIDER;
    private static final IdType SERVICE_PROVIDER_ID = newRandomUuid();
    private static final Encounter.EncounterParticipantComponent ENCOUNTER_PARTICIPANT_COMPONENT;
    private static final Practitioner PRACTITIONER;
    private static final IdType PRACTITIONER_ID = newRandomUuid();
    private static final HumanName PRACTITIONER_NAME;
    private static final Appointment APPOINTMENT;
    private static final IdType APPOINTMENT_ID = newRandomUuid();
    private static final Location LOCATION;
    private static final IdType LOCATION_ID = newRandomUuid();
    private static final Encounter.EncounterLocationComponent ENCOUNTER_LOCATION_COMPONENT;
    private static final Patient PATIENT;
    private static final IdType PATIENT_ID = newRandomUuid();
    private static final EpisodeOfCare EPISODE_OF_CARE;
    private static final IdType EPISODE_OF_CARE_ID = newRandomUuid();
    private static final ReferralRequest REFERRAL_REQUEST;
    private static final IdType REFERRAL_REQUEST_ID = newRandomUuid();
    private static final Composition COMPOSITION;
    private static final IdType COMPOSITION_ID = newRandomUuid();
    private static final ListResource LIST_RESOURCE;
    private static final IdType LIST_RESOURCE_ID = newRandomUuid();
    private static final CarePlan CAREPLAN;
    private static final IdType CAREPLAN_ID = newRandomUuid();
    private static final HealthcareService HEALTHCARE_SERVICE;
    private static final IdType HEALTHCARE_SERVICE_ID = newRandomUuid();
    private static final Consent CONSENT;
    private static final IdType CONSENT_ID = newRandomUuid();
    private static final Condition CONDITION;
    private static final IdType CONDITION_ID = newRandomUuid();
    private static final QuestionnaireResponse QUESTIONNAIRE_RESPONSE;
    private static final IdType QUESTIONNAIRE_RESPONSE_ID = newRandomUuid();
    private static final MessageHeader MESSAGE_HEADER;
    private static final IdType MESSAGE_HEADER_ID = newRandomUuid();

    static {
        SERVICE_PROVIDER = new Organization();
        SERVICE_PROVIDER.setIdElement(SERVICE_PROVIDER_ID);

        PRACTITIONER = new Practitioner();
        PRACTITIONER.setIdElement(PRACTITIONER_ID);
        PRACTITIONER.setActive(true);
        PRACTITIONER_NAME = new HumanName();
        PRACTITIONER.setName(Collections.singletonList(PRACTITIONER_NAME));
        ENCOUNTER_PARTICIPANT_COMPONENT = new Encounter.EncounterParticipantComponent();
        ENCOUNTER_PARTICIPANT_COMPONENT.setIndividual(new Reference(PRACTITIONER));
        ENCOUNTER_PARTICIPANT_COMPONENT.setIndividualTarget(PRACTITIONER);

        APPOINTMENT = new Appointment();
        APPOINTMENT.setIdElement(APPOINTMENT_ID);

        LOCATION = new Location();
        LOCATION.setIdElement(LOCATION_ID);
        ENCOUNTER_LOCATION_COMPONENT = new Encounter.EncounterLocationComponent();
        ENCOUNTER_LOCATION_COMPONENT.setLocation(new Reference(LOCATION));
        ENCOUNTER_LOCATION_COMPONENT.setLocationTarget(LOCATION);

        PATIENT = new Patient();
        PATIENT.setId(PATIENT_ID);

        EPISODE_OF_CARE = new EpisodeOfCare();
        EPISODE_OF_CARE.setId(EPISODE_OF_CARE_ID);

        REFERRAL_REQUEST = new ReferralRequest();
        REFERRAL_REQUEST.setId(REFERRAL_REQUEST_ID);

        CONDITION = new Condition();
        CONDITION.setId(CONDITION_ID);

        COMPOSITION = new Composition();
        COMPOSITION.setId(COMPOSITION_ID);

        LIST_RESOURCE = new ListResource();
        LIST_RESOURCE.setId(LIST_RESOURCE_ID);

        CAREPLAN = new CarePlan();
        CAREPLAN.setId(CAREPLAN_ID);

        HEALTHCARE_SERVICE = new HealthcareService();
        HEALTHCARE_SERVICE.setId(HEALTHCARE_SERVICE_ID);

        CONSENT = new Consent();
        CONSENT.setId(CONSENT_ID);

        QUESTIONNAIRE_RESPONSE = new QuestionnaireResponse();
        QUESTIONNAIRE_RESPONSE.setId(QUESTIONNAIRE_RESPONSE_ID);

        MESSAGE_HEADER = new MessageHeader();
        MESSAGE_HEADER.setId(MESSAGE_HEADER_ID);

        ENCOUNTER = new Encounter();
        ENCOUNTER.setStatus(FINISHED);
        ENCOUNTER.setIdElement(ENCOUNTER_ID);
        ENCOUNTER.setParticipant(Collections.singletonList(ENCOUNTER_PARTICIPANT_COMPONENT));
        ENCOUNTER.setServiceProviderTarget(SERVICE_PROVIDER);
        ENCOUNTER.setAppointment(new Reference(APPOINTMENT));
        ENCOUNTER.setAppointmentTarget(APPOINTMENT);
        ENCOUNTER.setLocation(Collections.singletonList(ENCOUNTER_LOCATION_COMPONENT));
        ENCOUNTER.setSubject(new Reference(PATIENT));
        ENCOUNTER.setSubjectTarget(PATIENT);
        ENCOUNTER.addEpisodeOfCare(new Reference(EPISODE_OF_CARE));
        ENCOUNTER.addIncomingReferral(new Reference(REFERRAL_REQUEST));
    }

    @InjectMocks
    private EncounterReportBundleService encounterReportBundleService;
    @Mock
    private EncounterMapper encounterMapper;
    @Mock
    private CompositionMapper compositionMapper;
    @Mock
    private ListMapper listMapper;
    @Mock
    private CarePlanMapper carePlanMapper;
    @Mock
    private HealthcareServiceMapper healthcareServiceMapper;
    @Mock
    private ConsentMapper consentMapper;
    @Mock
    private PathwayUtil pathwayUtil;
    @Mock
    private MessageHeaderService messageHeaderService;

    @BeforeEach
    public void setUp() throws XmlException {
        List<QuestionnaireResponse> questionnaireResponseList = new ArrayList<>();
        questionnaireResponseList.add(QUESTIONNAIRE_RESPONSE);
        when(encounterMapper.mapEncounter(any(), any())).thenReturn(ENCOUNTER);
        when(compositionMapper.mapComposition(any(), any())).thenReturn(COMPOSITION);
        when(listMapper.mapList(any(), any(), any())).thenReturn(LIST_RESOURCE);
        when(carePlanMapper.mapCarePlan(any(), any())).thenReturn(Collections.singletonList(CAREPLAN));
        when(healthcareServiceMapper.mapHealthcareService(any())).thenReturn(Collections.singletonList(HEALTHCARE_SERVICE));
        when(consentMapper.mapConsent(any(), any())).thenReturn(CONSENT);
        when(pathwayUtil.getQuestionnaireResponses(any(), any(), any())).thenReturn(questionnaireResponseList);
        when(messageHeaderService.createMessageHeader()).thenReturn(MESSAGE_HEADER);
        Encounter.DiagnosisComponent diagnosisComponent = new Encounter.DiagnosisComponent();
        diagnosisComponent.setCondition(new Reference());
        diagnosisComponent.setRole(new CodeableConcept());
        diagnosisComponent.setRank(1);
        diagnosisComponent.setConditionTarget(CONDITION);
        ENCOUNTER.addDiagnosis(diagnosisComponent);
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldCreateEncounterBundle() throws XmlException {
        POCDMT000002UK01ClinicalDocument1 document = mock(POCDMT000002UK01ClinicalDocument1.class);

        Bundle encounterBundle = encounterReportBundleService.createEncounterBundle(document);
        assertThat(encounterBundle.getType()).isEqualTo(MESSAGE);
        assertThat(encounterBundle.getEntry().size()).isEqualTo(16);
        List<BundleEntryComponent> entries = encounterBundle.getEntry();
        verifyEntry(entries.get(0), MESSAGE_HEADER_ID.getValue(), ResourceType.MessageHeader);
        verifyEntry(entries.get(1), ENCOUNTER_ID.getValue(), ResourceType.Encounter);
        verifyEntry(entries.get(2), SERVICE_PROVIDER_ID.getValue(), ResourceType.Organization);
        verifyEntry(entries.get(3), PRACTITIONER_ID.getValue(), ResourceType.Practitioner);
        verifyEntry(entries.get(4), LOCATION_ID.getValue(), ResourceType.Location);
        verifyEntry(entries.get(5), PATIENT_ID.getValue(), ResourceType.Patient);
        verifyEntry(entries.get(6), HEALTHCARE_SERVICE_ID.getValue(), ResourceType.HealthcareService);
        verifyEntry(entries.get(7), REFERRAL_REQUEST_ID.getValue(), ResourceType.ReferralRequest);
        verifyEntry(entries.get(8), APPOINTMENT_ID.getValue(), ResourceType.Appointment);
        verifyEntry(entries.get(9), EPISODE_OF_CARE_ID.getValue(), ResourceType.EpisodeOfCare);
        verifyEntry(entries.get(10), COMPOSITION_ID.getValue(), ResourceType.Composition);
        verifyEntry(entries.get(11), CAREPLAN_ID.getValue(), ResourceType.CarePlan);
        verifyEntry(entries.get(12), CONSENT_ID.getValue(), ResourceType.Consent);
        verifyEntry(entries.get(13), CONDITION_ID.getValue(), ResourceType.Condition);
        verifyEntry(entries.get(14), QUESTIONNAIRE_RESPONSE_ID.getValue(), ResourceType.QuestionnaireResponse);
        verifyEntry(entries.get(15), LIST_RESOURCE_ID.getValue(), ResourceType.List);
    }

    private void verifyEntry(BundleEntryComponent entry, String fullUrl, ResourceType resourceType) {
        assertThat(entry.getFullUrl()).isEqualTo(fullUrl);
        assertThat(entry.getResource().getResourceType()).isEqualTo(resourceType);
    }
}
