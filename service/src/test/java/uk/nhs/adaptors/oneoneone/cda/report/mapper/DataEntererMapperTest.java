package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01DataEnterer;
import uk.nhs.connect.iucds.cda.ucr.TS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataEntererMapperTest {

    @Mock
    private PractitionerMapper practitionerMapper;

    @Mock
    private PeriodMapper periodMapper;

    @InjectMocks
    private DataEntererMapper dataEntererMapper;

    @Mock
    private Practitioner practitioner;

    @Mock
    private Period period;

    @Test
    public void shouldMapDataEntererToParticipantComponent() {
        POCDMT000002UK01DataEnterer dataEnterer = mock(POCDMT000002UK01DataEnterer.class);
        POCDMT000002UK01AssignedEntity assignedEntity = mock(POCDMT000002UK01AssignedEntity.class);
        TS time = mock(TS.class);

        when(dataEnterer.getAssignedEntity()).thenReturn(assignedEntity);
        when(dataEnterer.isSetTime()).thenReturn(true);
        when(dataEnterer.getTime()).thenReturn(time);
        when(dataEnterer.getTypeCode()).thenReturn("CON");
        when(practitionerMapper.mapPractitioner(isA(POCDMT000002UK01AssignedEntity.class)))
            .thenReturn(practitioner);
        when(periodMapper.mapPeriod(isA(TS.class))).thenReturn(period);

        Encounter.EncounterParticipantComponent participantComponent = dataEntererMapper
            .mapDataEntererIntoParticipantComponent(dataEnterer);

        assertThat(participantComponent.getIndividualTarget()).isEqualTo(practitioner);
        assertThat(participantComponent.getPeriod()).isEqualTo(period);
        assertThat(participantComponent.getType().get(0).getText()).isEqualTo("CON");
    }
}
