package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.assertj.core.util.Arrays;
import org.hl7.fhir.dstu3.model.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RecordTarget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupMapperTest {

    @InjectMocks
    private GroupMapper groupMapper;

    @Mock
    private Group.GroupMemberComponent groupMemberComponent;

    @Mock
    private Group group;

    @Test
    public void mapGroupTest() {
        POCDMT000002UK01RecordTarget[] recordTarget = Arrays.array(mock(POCDMT000002UK01RecordTarget.class));

        when(groupMapper.mapGroup(any())).thenReturn(group);

        group = groupMapper.mapGroup(recordTarget);

        assertThat(group.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(group.getActive()).isEqualTo(true);
        assertThat(group.getType().compareTo(Group.GroupType.PERSON));
        assertThat(group.getMemberFirstRep()).isEqualTo(groupMemberComponent);
    }
}