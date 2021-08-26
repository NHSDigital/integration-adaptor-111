package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.util.Arrays;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01PatientRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RecordTarget;

@ExtendWith(MockitoExtension.class)
public class GroupMapperTest {

    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private GroupMapper groupMapper;

    @Mock
    private Group group;

    @Mock
    private Patient fhirPatient;

    @Mock
    private ResourceUtil resourceUtil;

    @Test
    public void mapGroupTest() {
        POCDMT000002UK01RecordTarget recordTarget = mock(POCDMT000002UK01RecordTarget.class);
        POCDMT000002UK01PatientRole patientRole = mock(POCDMT000002UK01PatientRole.class);
        when(recordTarget.getPatientRole()).thenReturn(patientRole);
        when(patientMapper.mapPatient(any())).thenReturn(fhirPatient);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
        when(resourceUtil.createReference(fhirPatient)).thenReturn(new Reference(fhirPatient));

        group = groupMapper.mapGroup(Arrays.array(recordTarget));

        assertThat(group.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(group.getActive()).isEqualTo(true);
        assertThat(group.getType().compareTo(Group.GroupType.PERSON)).isEqualTo(0);
        assertThat(group.getMemberFirstRep().getEntity().getResource()).isEqualTo(fhirPatient);
        assertThat(group.getMemberFirstRep().getEntityTarget()).isEqualTo(fhirPatient);
    }
}