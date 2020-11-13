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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedAuthor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Organization;

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
    private POCDMT000002UK01AssignedAuthor assignedAuthor;

    private static final String CODE = "T1";
    private static final String CODE_SYSTEM = "1.2.3.4.5.6.7";
    private static final String DISPLAY_NAME = "Nurse";

    @BeforeEach
    public void setUp() {
        when(author.getAssignedAuthor()).thenReturn(assignedAuthor);
        CE ce = mock(CE.class);
        when(ce.getCode()).thenReturn(CODE);
        when(ce.getCodeSystem()).thenReturn(CODE_SYSTEM);
        when(ce.getDisplayName()).thenReturn(DISPLAY_NAME);
        when(assignedAuthor.getCode()).thenReturn(ce);
        when(assignedAuthor.getRepresentedOrganization()).thenReturn(mock(POCDMT000002UK01Organization.class));
        when(organizationMapper.mapOrganization(any(POCDMT000002UK01Organization.class))).thenReturn(organization);
        when(practitionerMapper.mapPractitioner(any(POCDMT000002UK01AssignedAuthor.class))).thenReturn(practitioner);
    }

    @Test
    public void shouldMapAuthorRoles() {
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
}
