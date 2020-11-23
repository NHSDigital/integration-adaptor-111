package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedAuthor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ResponsibleParty;

@ExtendWith(MockitoExtension.class)
public class PractitionerRoleMapperTest {
    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private PractitionerMapper practitionerMapper;

    @InjectMocks
    private PractitionerRoleMapper practitionerRoleMapper;

    @Mock
    private Organization organization;

    @Mock
    private Practitioner practitioner;

    @Mock
    private POCDMT000002UK01Author author;

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    private static final String CODE = "T1";
    private static final String CODE_SYSTEM = "1.2.3.4.5.6.7";
    private static final String DISPLAY_NAME = "Nurse";

    @Test
    public void shouldMapAuthorRoles() {
        mockAuthors();

        POCDMT000002UK01Author[] authors = new POCDMT000002UK01Author[] {author};

        List<PractitionerRole> practitionerRoles = practitionerRoleMapper.mapAuthorRoles(authors);

        assertThat(practitionerRoles.size()).isEqualTo(1);
        PractitionerRole role = practitionerRoles.get(0);
        Coding codingFirstRep = role.getCodeFirstRep().getCodingFirstRep();
        assertThat(codingFirstRep.getCode()).isEqualTo(CODE);
        assertThat(codingFirstRep.getSystem()).isEqualTo(CODE_SYSTEM);
        assertThat(codingFirstRep.getDisplay()).isEqualTo(DISPLAY_NAME);
        assertThat(role.getPractitionerTarget()).isEqualTo(practitioner);
        assertThat(role.getOrganizationTarget()).isEqualTo(organization);
    }

    @Test
    public void shouldMapResponsibleParty() {
        mockResponsibleParty();

        PractitionerRole role = practitionerRoleMapper.mapResponsibleParty(clinicalDocument).get();

        Coding codingFirstRep = role.getCodeFirstRep().getCodingFirstRep();
        assertThat(codingFirstRep.getCode()).isEqualTo(CODE);
        assertThat(codingFirstRep.getSystem()).isEqualTo(CODE_SYSTEM);
        assertThat(codingFirstRep.getDisplay()).isEqualTo(DISPLAY_NAME);
        assertThat(role.getOrganizationTarget()).isEqualTo(organization);
        assertThat(role.getPractitionerTarget()).isEqualTo(practitioner);
    }

    private void mockAuthors() {
        POCDMT000002UK01AssignedAuthor assignedAuthor = mock(POCDMT000002UK01AssignedAuthor.class);
        when(author.getAssignedAuthor()).thenReturn(assignedAuthor);
        CE ce = mockCode();
        when(assignedAuthor.getCode()).thenReturn(ce);
        when(assignedAuthor.getRepresentedOrganization()).thenReturn(mock(POCDMT000002UK01Organization.class));
        when(organizationMapper.mapOrganization(any(POCDMT000002UK01Organization.class))).thenReturn(organization);
        when(practitionerMapper.mapPractitioner(any(POCDMT000002UK01AssignedAuthor.class))).thenReturn(practitioner);
    }

    private void mockResponsibleParty() {
        when(clinicalDocument.isSetComponentOf()).thenReturn(true);
        POCDMT000002UK01Component1 component1 = mock(POCDMT000002UK01Component1.class);
        when(clinicalDocument.getComponentOf()).thenReturn(component1);
        POCDMT000002UK01EncompassingEncounter encompassingEncounter = mock(POCDMT000002UK01EncompassingEncounter.class);
        when(component1.getEncompassingEncounter()).thenReturn(encompassingEncounter);
        when(encompassingEncounter.isSetResponsibleParty()).thenReturn(true);
        POCDMT000002UK01ResponsibleParty responsibleParty = mock(POCDMT000002UK01ResponsibleParty.class);
        when(encompassingEncounter.getResponsibleParty()).thenReturn(responsibleParty);
        POCDMT000002UK01AssignedEntity assignedEntity = mock(POCDMT000002UK01AssignedEntity.class);
        when(assignedEntity.isSetRepresentedOrganization()).thenReturn(true);
        when(assignedEntity.getRepresentedOrganization()).thenReturn(mock(POCDMT000002UK01Organization.class));
        CE ce = mockCode();
        when(assignedEntity.getCode()).thenReturn(ce);
        when(responsibleParty.getAssignedEntity()).thenReturn(assignedEntity);
        when(organizationMapper.mapOrganization(any(POCDMT000002UK01Organization.class))).thenReturn(organization);
        when(practitionerMapper.mapPractitioner(any(POCDMT000002UK01AssignedEntity.class))).thenReturn(practitioner);
    }

    private CE mockCode() {
        CE ce = mock(CE.class);
        when(ce.getCode()).thenReturn(CODE);
        when(ce.getCodeSystem()).thenReturn(CODE_SYSTEM);
        when(ce.getDisplayName()).thenReturn(DISPLAY_NAME);
        return ce;
    }
}
