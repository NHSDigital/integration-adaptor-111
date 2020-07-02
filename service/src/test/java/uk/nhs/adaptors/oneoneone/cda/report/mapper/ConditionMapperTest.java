package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ConditionMapperTest {

    @InjectMocks
    private ConditionMapper conditionMapper;

    @Mock
    private POCDMT000002UK01Encounter encounter;

    @Test
    public void mapCondition() {
        Condition condition = conditionMapper.mapCondition(encounter);

        assertThat(condition.getClinicalStatus()).isEqualTo(Condition.ConditionClinicalStatus.NULL);
        assertThat(condition.getVerificationStatus()).isEqualTo(Condition.ConditionVerificationStatus.UNKNOWN);
    }
}
