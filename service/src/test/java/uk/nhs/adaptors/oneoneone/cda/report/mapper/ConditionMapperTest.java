package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.ACTIVE;
import static org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus.UNKNOWN;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConditionMapperTest {

    private static final String EFFECTIVE_TIME_STRING = "201706011400+00";
    private final Reference individualRef = new Reference("IndividualRef/1");
    @InjectMocks
    private ConditionMapper conditionMapper;

    @Mock
    private POCDMT000002UK01Encounter itkEncounter;
    @Mock
    private Encounter encounter;
    @Mock
    private IVLTS time;
    @Mock
    private Encounter.EncounterParticipantComponent participantFirstRep;

    @BeforeEach
    public void setUp() {
        when(itkEncounter.getEffectiveTime()).thenReturn(time);
        when(time.getValue()).thenReturn(EFFECTIVE_TIME_STRING);
        when(encounter.getParticipantFirstRep()).thenReturn(participantFirstRep);
        when(participantFirstRep.getIndividual()).thenReturn(individualRef);
    }

    @Test
    public void mapCondition() {
        Condition condition = conditionMapper.mapCondition(itkEncounter, encounter);

        assertThat(condition.getClinicalStatus()).isEqualTo(ACTIVE);
        assertThat(condition.getVerificationStatus()).isEqualTo(UNKNOWN);
        assertThat(condition.getAsserter()).isEqualTo(individualRef);
        assertThat(condition.getAssertedDate()).isEqualTo(DateUtil.parse(EFFECTIVE_TIME_STRING));
    }
}
