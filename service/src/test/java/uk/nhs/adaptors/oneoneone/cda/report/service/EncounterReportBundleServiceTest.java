package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;
import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
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
    private static final Organization ORGANIZATION;
    private static final IdType ORGANIZATION_ID = newRandomUuid();

    static {
        ENCOUNTER = new Encounter();
        ENCOUNTER.setStatus(FINISHED);
        ENCOUNTER.setIdElement(ENCOUNTER_ID);
        ORGANIZATION = new Organization();
        ORGANIZATION.setIdElement(ORGANIZATION_ID);
    }

    @Before
    public void setUp() {
        when(encounterMapper.mapEncounter(any())).thenReturn(ENCOUNTER);
        when(serviceProviderMapper.mapServiceProvider()).thenReturn(ORGANIZATION);
    }

    @Test
    public void createEncounterBundle() {
        POCDMT000002UK01ClinicalDocument1 document = mock(POCDMT000002UK01ClinicalDocument1.class);

        Bundle encounterBundle = encounterReportBundleService.createEncounterBundle(document);

        assertThat(encounterBundle.getEntry().size()).isEqualTo(2);
        List<BundleEntryComponent> entries = encounterBundle.getEntry();
        verifyEntry(entries.get(0), ENCOUNTER_ID.getValue(), ResourceType.Encounter);
        verifyEntry(entries.get(1), ORGANIZATION_ID.getValue(), ResourceType.Organization);
    }

    private void verifyEntry(BundleEntryComponent entry, String fullUrl, ResourceType resourceType) {
        assertThat(entry.getFullUrl()).isEqualTo(fullUrl);
        assertThat(entry.getResource().getResourceType()).isEqualTo(resourceType);
    }
}
