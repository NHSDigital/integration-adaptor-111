package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ResponsibleParty;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.EpisodeOfCare.EpisodeOfCareStatus.ACTIVE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EpisodeOfCareMapperTest {

    private static final String CODE = "CODE";

    @Mock
    private PractitionerMapper practitionerMapper;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Mock
    private POCDMT000002UK01EncompassingEncounter encompassingEncounter;

    @Mock
    private PeriodMapper periodMapper;

    @InjectMocks
    private EpisodeOfCareMapper episodeOfCareMapper;

    @BeforeEach
    public void setUp() {
        POCDMT000002UK01Component1 component = mock(POCDMT000002UK01Component1.class);
        when(clinicalDocument.getComponentOf()).thenReturn(component);
        when(component.getEncompassingEncounter()).thenReturn(encompassingEncounter);
    }

    @Test
    public void shouldMapEpisodeOfCare() {
        when(clinicalDocument.isSetComponentOf()).thenReturn(true);
        when(encompassingEncounter.isSetResponsibleParty()).thenReturn(true);
        POCDMT000002UK01ResponsibleParty responsibleParty = mock(POCDMT000002UK01ResponsibleParty.class);
        when(encompassingEncounter.getResponsibleParty()).thenReturn(responsibleParty);
        CE code = mock(CE.class);
        when(encompassingEncounter.getCode()).thenReturn(code);
        when(code.getCode()).thenReturn(CODE);
        POCDMT000002UK01AssignedEntity assignedEntity = mock(POCDMT000002UK01AssignedEntity.class);
        when(responsibleParty.getAssignedEntity()).thenReturn(assignedEntity);
        Patient patient = mock(Patient.class);
        Reference patientReference = new Reference(patient);
        Practitioner practitioner = mock(Practitioner.class);
        when(practitionerMapper.mapPractitioner(assignedEntity)).thenReturn(practitioner);
        when(assignedEntity.isSetRepresentedOrganization()).thenReturn(true);
        POCDMT000002UK01Organization organization = mock(POCDMT000002UK01Organization.class);
        when(assignedEntity.getRepresentedOrganization()).thenReturn(organization);
        Organization fhirOrganization = mock(Organization.class);
        when(organizationMapper.mapOrganization(organization)).thenReturn(fhirOrganization);

        Optional<EpisodeOfCare> episodeOfCareOptional = episodeOfCareMapper.mapEpisodeOfCare(clinicalDocument, patientReference);

        assertThat(episodeOfCareOptional).isNotEmpty();
        EpisodeOfCare episodeOfCare = episodeOfCareOptional.get();
        assertThat(episodeOfCare.getStatus()).isEqualTo(ACTIVE);
        assertThat(episodeOfCare.getType().get(0).getText()).isEqualTo(CODE);
        assertThat(episodeOfCare.getPatient()).isEqualTo(patientReference);
        assertThat(episodeOfCare.getCareManager().getResource()).isEqualTo(practitioner);
        assertThat(episodeOfCare.getManagingOrganization().getResource()).isEqualTo(fhirOrganization);
    }

    @Test
    public void shouldMapEpisodeOfCareNoResponsibleParty() {
        when(clinicalDocument.isSetComponentOf()).thenReturn(true);
        when(encompassingEncounter.isSetResponsibleParty()).thenReturn(false);

        Optional<EpisodeOfCare> episodeOfCareOptional = episodeOfCareMapper.mapEpisodeOfCare(clinicalDocument, null);

        assertThat(episodeOfCareOptional).isEmpty();
    }
}
