package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.assertj.core.util.Arrays;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RecordTarget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupMapperTest {

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private GroupMapper groupMapper;

    @Mock
    private Group group;

    @Mock
    private Patient fhirPatient;

    @Test
    public void mapGroupTest() {
        POCDMT000002UK01RecordTarget recordTarget = mock(POCDMT000002UK01RecordTarget.class);
        POCDMT000002UK01PatientRole patientRole = mock(POCDMT000002UK01PatientRole.class);
        when(recordTarget.getPatientRole()).thenReturn(patientRole);
        when(patientMapper.mapPatient(any())).thenReturn(fhirPatient);

        group = groupMapper.mapGroup(Arrays.array(recordTarget));

        assertThat(group.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(group.getActive()).isEqualTo(true);
        assertThat(group.getType().compareTo(Group.GroupType.PERSON));
        assertThat(group.getMemberFirstRep().getEntity().getResource()).isEqualTo(fhirPatient);
        assertThat(group.getMemberFirstRep().getEntityTarget()).isEqualTo(fhirPatient);
    }
}