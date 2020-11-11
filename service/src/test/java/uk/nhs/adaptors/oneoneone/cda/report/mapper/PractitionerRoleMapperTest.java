package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedAuthor;

import static org.assertj.core.api.Assertions.assertThat;


public class PractitionerRoleMapperTest {
    @InjectMocks
    private PractitionerRoleMapper practitionerRoleMapper;

    @Mock
    private POCDMT000002UK01AssignedAuthor assignedAuthor;
    @Mock
    private CE assignedAuthorCode;

    private static final String CODE = "T1";
    private static final String CODESYSTEM="2.16.840.1.113883.2.1.3.2.4.17.196";
    private static final String DISPLAYNAME="Nurse Triage Practitioner";

    @BeforeEach
    public void setup() {
        when(assignedAuthor.getCode()).thenReturn(assignedAuthorCode);
        when(assignedAuthorCode.getDisplayName()).thenReturn(CODE);
        when(assignedAuthorCode.getDisplayName()).thenReturn(DISPLAYNAME);
        when(assignedAuthorCode.getDisplayName()).thenReturn(CODESYSTEM);
    }

    @Test
    public void shouldMapPractitionerRoleFromAssignedAuthor() {
        PractitionerRoleMapper practitionerRoleMapper = new PractitionerRoleMapper();

        PractitionerRole practitionerRole = practitionerRoleMapper.mapPractitioner(assignedAuthor);
        Coding practitionerRoleCoding = practitionerRole.getCodeFirstRep().getCodingFirstRep();

        assertThat(practitionerRoleCoding.getCode()).isEqualTo(CODE);
        assertThat(practitionerRoleCoding.getDisplay()).isEqualTo(DISPLAYNAME);
        assertThat(practitionerRoleCoding.getSystem()).isEqualTo(CODESYSTEM);
    }
}
