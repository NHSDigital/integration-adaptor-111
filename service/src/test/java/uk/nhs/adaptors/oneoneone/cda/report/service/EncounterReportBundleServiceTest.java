package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.adaptors.oneoneone.cda.report.mapper.EncounterMapper;
import uk.nhs.adaptors.oneoneone.cda.report.mapper.ServiceProviderMapper;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Custodian;

@RunWith(MockitoJUnitRunner.class)
public class EncounterReportBundleServiceTest {
    @InjectMocks
    private EncounterReportBundleService encounterReportBundleService;

    @Mock
    private EncounterMapper encounterMapper;

    @Mock
    private ServiceProviderMapper serviceProviderMapper;

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

    static {
        ENCOUNTER = new Encounter();
        ENCOUNTER.setStatus(FINISHED);
        ENCOUNTER.setIdElement(ENCOUNTER_ID);
        SERVICE_PROVIDER = new Organization();
        SERVICE_PROVIDER.setIdElement(SERVICE_PROVIDER_ID);
        ENCOUNTER_PARTICIPANT_COMPONENT = new Encounter.EncounterParticipantComponent();
        PRACTITIONER = new Practitioner();
        PRACTITIONER.setIdElement(PRACTITIONER_ID);
        PRACTITIONER.setActive(true);
        PRACTITIONER_NAME = new HumanName();
        PRACTITIONER.setName(Collections.singletonList(PRACTITIONER_NAME));
        ENCOUNTER_PARTICIPANT_COMPONENT.setIndividual(new Reference(PRACTITIONER));
        ENCOUNTER_PARTICIPANT_COMPONENT.setIndividualTarget(PRACTITIONER);
        ENCOUNTER.setParticipant(Collections.singletonList(ENCOUNTER_PARTICIPANT_COMPONENT));
        APPOINTMENT = new Appointment();
        APPOINTMENT.setIdElement(APPOINTMENT_ID);
        ENCOUNTER.setAppointmentTarget(APPOINTMENT);
        ENCOUNTER.setServiceProviderTarget(SERVICE_PROVIDER);
    }

    @Before
    public void setUp() {
        when(encounterMapper.mapEncounter(any())).thenReturn(ENCOUNTER);
    }

    @Test
    public void createEncounterBundle() {
        POCDMT000002UK01ClinicalDocument1 document = mock(POCDMT000002UK01ClinicalDocument1.class);

        Bundle encounterBundle = encounterReportBundleService.createEncounterBundle(document);

        assertThat(encounterBundle.getEntry().size()).isEqualTo(4);
        List<BundleEntryComponent> entries = encounterBundle.getEntry();
        verifyEntry(entries.get(0), ENCOUNTER_ID.getValue(), ResourceType.Encounter);
        verifyEntry(entries.get(1), SERVICE_PROVIDER_ID.getValue(), ResourceType.Organization);
        verifyEntry(entries.get(2), PRACTITIONER_ID.getValue(), ResourceType.Practitioner);
        verifyEntry(entries.get(3), APPOINTMENT_ID.getValue(), ResourceType.Appointment);
    }

    private void verifyEntry(BundleEntryComponent entry, String fullUrl, ResourceType resourceType) {
        assertThat(entry.getFullUrl()).isEqualTo(fullUrl);
        assertThat(entry.getResource().getResourceType()).isEqualTo(resourceType);
    }
}
