package uk.nhs.adaptors.oneoneone.cda.report.service;

import static java.math.BigInteger.TWO;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Bundle.BundleType.MESSAGE;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Consent;
import org.hl7.fhir.dstu3.model.Device;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.MessageHeader;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkReportHeader;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.CarePlanMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.CompositionMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ConditionMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ConsentMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.DeviceMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.HealthcareServiceMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ListMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ObservationMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.PractitionerRoleMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ReferralRequestMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.RelatedPersonMapper;
import uk.nhs.adaptors.oneoneone.cda.report.util.PathwayUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.INT;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.TS;

@ExtendWith(MockitoExtension.class)
public class EncounterReportBundleServiceTest {
    private static final String SPECIFICATION_KEY = "urn:nhs-itk:ns:201005:interaction";
    private static final String SPECIFICATION_VALUE = "urn:nhs-itk:interaction:primaryEmergencyDepartmentRecipientNHS111CDADocument-v2-0";
    private static final Encounter ENCOUNTER;
    private static final Device DEVICE;
    private static final IdType ENCOUNTER_ID = getRandomUUID();
    private static final Organization SERVICE_PROVIDER;
    private static final IdType SERVICE_PROVIDER_ID = getRandomUUID();
    private static final Encounter.EncounterParticipantComponent ENCOUNTER_PARTICIPANT_COMPONENT;
    private static final Practitioner PRACTITIONER;
    private static final IdType PRACTITIONER_ID = getRandomUUID();
    private static final HumanName PRACTITIONER_NAME;
    private static final Appointment APPOINTMENT;
    private static final IdType APPOINTMENT_ID = getRandomUUID();
    private static final Location LOCATION;
    private static final IdType LOCATION_ID = getRandomUUID();
    private static final Encounter.EncounterLocationComponent ENCOUNTER_LOCATION_COMPONENT;
    private static final Patient PATIENT;
    private static final IdType PATIENT_ID = getRandomUUID();
    private static final ReferralRequest REFERRAL_REQUEST;
    private static final IdType REFERRAL_REQUEST_ID = getRandomUUID();
    private static final Composition COMPOSITION;
    private static final IdType COMPOSITION_ID = getRandomUUID();
    private static final ListResource LIST_RESOURCE;
    private static final IdType LIST_RESOURCE_ID = getRandomUUID();
    private static final CarePlan CAREPLAN;
    private static final IdType CAREPLAN_ID = getRandomUUID();
    private static final HealthcareService HEALTHCARE_SERVICE;
    private static final IdType HEALTHCARE_SERVICE_ID = getRandomUUID();
    private static final Consent CONSENT;
    private static final IdType CONSENT_ID = getRandomUUID();
    private static final Condition CONDITION;
    private static final IdType CONDITION_ID = getRandomUUID();
    private static final QuestionnaireResponse QUESTIONNAIRE_RESPONSE;
    private static final IdType QUESTIONNAIRE_RESPONSE_ID = getRandomUUID();
    private static final MessageHeader MESSAGE_HEADER;
    private static final IdType MESSAGE_HEADER_ID = getRandomUUID();
    private static final Observation OBSERVATION;
    private static final IdType OBSERVATION_ID = getRandomUUID();
    private static final PractitionerRole AUTHOR_ROLE;
    private static final IdType AUTHOR_ROLE_ID = getRandomUUID();
    private static final Organization AUTHOR_ORG;
    private static final IdType AUTHOR_ORG_ID = getRandomUUID();
    private static final PractitionerRole PRACTITIONER_ROLE;
    private static final IdType PRACTITIONER_ROLE_ID = getRandomUUID();
    private static final Organization PRACTITIONER_ORG;
    private static final IdType PRACTITIONER_ORG_ID = getRandomUUID();
    private static final BigInteger VERSION = TWO;
    private static final String MESSAGEID = getRandomUUID().toString();
    private static final RelatedPerson RELATED_PERSON;
    private static final IdType RELATED_PERSON_ID = getRandomUUID();
    private static final IdType DEVICE_ID = getRandomUUID();
    private static final String EFFECTIVE_TIME = "20210406123335+01";

    private static IdType getRandomUUID() {
        return new IdType(UUID.randomUUID().toString());
    }

    static {
        SERVICE_PROVIDER = new Organization();
        SERVICE_PROVIDER.setIdElement(SERVICE_PROVIDER_ID);

        PRACTITIONER = new Practitioner();
        PRACTITIONER.setIdElement(PRACTITIONER_ID);
        PRACTITIONER.setActive(true);
        PRACTITIONER_NAME = new HumanName();
        PRACTITIONER.setName(singletonList(PRACTITIONER_NAME));
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

        OBSERVATION = new Observation();
        OBSERVATION.setId(OBSERVATION_ID);

        AUTHOR_ROLE = new PractitionerRole();
        AUTHOR_ROLE.setId(AUTHOR_ROLE_ID);

        AUTHOR_ORG = new Organization();
        AUTHOR_ORG.setId(AUTHOR_ORG_ID);
        AUTHOR_ROLE.setOrganizationTarget(AUTHOR_ORG);

        PRACTITIONER_ORG = new Organization();
        PRACTITIONER_ORG.setId(PRACTITIONER_ORG_ID);
        PRACTITIONER_ROLE = new PractitionerRole();
        PRACTITIONER_ROLE.setId(PRACTITIONER_ROLE_ID);
        PRACTITIONER_ROLE.setOrganizationTarget(PRACTITIONER_ORG);

        ENCOUNTER = new Encounter();
        ENCOUNTER.setStatus(FINISHED);
        ENCOUNTER.setIdElement(ENCOUNTER_ID);
        ENCOUNTER.setParticipant(singletonList(ENCOUNTER_PARTICIPANT_COMPONENT));
        ENCOUNTER.setServiceProviderTarget(SERVICE_PROVIDER);
        ENCOUNTER.setAppointment(new Reference(APPOINTMENT));
        ENCOUNTER.setAppointmentTarget(APPOINTMENT);
        ENCOUNTER.setLocation(singletonList(ENCOUNTER_LOCATION_COMPONENT));
        ENCOUNTER.setSubject(new Reference(PATIENT));
        ENCOUNTER.setSubjectTarget(PATIENT);
        ENCOUNTER.addIncomingReferral(new Reference(REFERRAL_REQUEST));

        RELATED_PERSON = new RelatedPerson();
        RELATED_PERSON.setIdElement(RELATED_PERSON_ID);

        DEVICE = new Device();
        DEVICE.setIdElement(DEVICE_ID);
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
    @Mock
    private ConditionMapper conditionMapper;
    @Mock
    private ReferralRequestMapper referralRequestMapper;
    @Mock
    private ObservationMapper observationMapper;
    @Mock
    private PractitionerRoleMapper practitionerRoleMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 document;
    @Mock
    private ResourceUtil resourceUtil;
    @Mock
    private RelatedPersonMapper relatedPersonMapper;
    @Mock
    private TS ts;
    @Mock
    private Reference reference;
    @Mock
    private DeviceMapper deviceMapper;

    @BeforeEach
    public void setUp() throws XmlException {
        INT versionNumber = mock(INT.class);
        when(versionNumber.getValue()).thenReturn(VERSION);
        when(document.getVersionNumber()).thenReturn(versionNumber);
        when(document.getEffectiveTime()).thenReturn(ts);
        when(ts.getValue()).thenReturn(EFFECTIVE_TIME);
        List<QuestionnaireResponse> questionnaireResponseList = new ArrayList<>();
        questionnaireResponseList.add(QUESTIONNAIRE_RESPONSE);
        when(deviceMapper.mapDevice()).thenReturn(DEVICE);
        when(encounterMapper.mapEncounter(any(), any(), any(), any())).thenReturn(ENCOUNTER);
        when(conditionMapper.mapCondition(any(), any(), any())).thenReturn(CONDITION);
        when(compositionMapper.mapComposition(any(), any(), any(), any(), any(), any())).thenReturn(COMPOSITION);
        when(listMapper.mapList(any(), any(), any(), any())).thenReturn(LIST_RESOURCE);
        when(carePlanMapper.mapCarePlan(any(), any(), any())).thenReturn(singletonList(CAREPLAN));
        when(healthcareServiceMapper.mapHealthcareService(any())).thenReturn(singletonList(HEALTHCARE_SERVICE));
        when(consentMapper.mapConsent(any(), any())).thenReturn(CONSENT);
        when(pathwayUtil.getQuestionnaireResponses(any(), any(), any())).thenReturn(questionnaireResponseList);
        when(messageHeaderService.createMessageHeader(any(), any(), eq(EFFECTIVE_TIME))).thenReturn(MESSAGE_HEADER);
        when(referralRequestMapper.mapReferralRequest(any(), any(), any(), any(), any())).thenReturn(REFERRAL_REQUEST);
        when(observationMapper.mapObservations(any(), eq(ENCOUNTER))).thenReturn(Arrays.asList(OBSERVATION));
        when(practitionerRoleMapper.mapAuthorRoles(any())).thenReturn(singletonList(AUTHOR_ROLE));
        when(practitionerRoleMapper.mapResponsibleParty(any())).thenReturn(Optional.of(PRACTITIONER_ROLE));
        when(relatedPersonMapper.createEmergencyContactRelatedPerson(eq(document), eq(ENCOUNTER))).thenReturn(RELATED_PERSON);
        when(deviceMapper.mapDevice()).thenReturn(DEVICE);
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
        ItkReportHeader itkReportHeader = new ItkReportHeader();
        itkReportHeader.setSpecKey(SPECIFICATION_KEY);
        itkReportHeader.setSpecVal(SPECIFICATION_VALUE);

        Bundle encounterBundle = encounterReportBundleService.createEncounterBundle(document, itkReportHeader, MESSAGEID);
        assertThat(encounterBundle.getType()).isEqualTo(MESSAGE);
        assertThat(encounterBundle.getIdentifier().getValue()).isEqualTo(TWO.toString());
        assertThat(encounterBundle.getEntry().size()).isEqualTo(22);
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
        verifyEntry(entries.get(9), COMPOSITION_ID.getValue(), ResourceType.Composition);
        verifyEntry(entries.get(10), CAREPLAN_ID.getValue(), ResourceType.CarePlan);
        verifyEntry(entries.get(11), CONSENT_ID.getValue(), ResourceType.Consent);
        verifyEntry(entries.get(12), CONDITION_ID.getValue(), ResourceType.Condition);
        verifyEntry(entries.get(13), QUESTIONNAIRE_RESPONSE_ID.getValue(), ResourceType.QuestionnaireResponse);
        verifyEntry(entries.get(14), OBSERVATION_ID.getValue(), ResourceType.Observation);
        verifyEntry(entries.get(15), AUTHOR_ROLE_ID.getValue(), ResourceType.PractitionerRole);
        verifyEntry(entries.get(16), AUTHOR_ORG_ID.getValue(), ResourceType.Organization);
        verifyEntry(entries.get(17), PRACTITIONER_ROLE_ID.getValue(), ResourceType.PractitionerRole);
        verifyEntry(entries.get(18), PRACTITIONER_ORG_ID.getValue(), ResourceType.Organization);
        verifyEntry(entries.get(19), RELATED_PERSON_ID.getValue(), ResourceType.RelatedPerson);
        verifyEntry(entries.get(20), DEVICE_ID.getValue(), ResourceType.Device);
        verifyEntry(entries.get(21), LIST_RESOURCE_ID.getValue(), ResourceType.List);

    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldMapIncomingReferralWithPractitioners() throws XmlException {
        ItkReportHeader itkReportHeader = new ItkReportHeader();
        List<Reference> recipients = new ArrayList<>();

        when(reference.getResource()).thenReturn(PRACTITIONER);
        recipients.add(reference);
        REFERRAL_REQUEST.setRecipient(recipients);

        Bundle encounterBundle = encounterReportBundleService.createEncounterBundle(document, itkReportHeader, MESSAGEID);
        List<BundleEntryComponent> entries = encounterBundle.getEntry();
        verifyEntry(entries.get(7), REFERRAL_REQUEST_ID.getValue(), ResourceType.ReferralRequest);
        verifyEntry(entries.get(8), PRACTITIONER_ID.getValue(), ResourceType.Practitioner);
    }

    private void verifyEntry(BundleEntryComponent entry, String fullUrl, ResourceType resourceType) {
        assertThat(entry.getFullUrl()).isEqualTo("urn:uuid:" + fullUrl);
        assertThat(entry.getResource().getResourceType()).isEqualTo(resourceType);
    }
}
