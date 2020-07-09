package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;

@RunWith(MockitoJUnitRunner.class)
public class InformantMapperTest {

    @Mock
    private PractitionerMapper practitionerMapper;

    @InjectMocks
    private InformantMapper informantMapper;

    @Mock
    private Practitioner practitioner;

    @Test
    public void shouldMapInformantToParticipantComponent() {
        POCDMT000002UK01Informant12 informant = mock(POCDMT000002UK01Informant12.class);
        POCDMT000002UK01AssignedEntity assignedEntity = mock(POCDMT000002UK01AssignedEntity.class);

        when(informant.getTypeCode()).thenReturn("CON");
        when(informant.isSetAssignedEntity()).thenReturn(true);
        when(informant.getAssignedEntity()).thenReturn(assignedEntity);
        when(practitionerMapper.mapPractitioner(isA(POCDMT000002UK01AssignedEntity.class)))
            .thenReturn(practitioner);

        Optional<Encounter.EncounterParticipantComponent> participantComponent = informantMapper
            .mapInformantIntoParticipantComponent(informant);

        assertThat(participantComponent.isPresent());
        assertThat(participantComponent.get().getIndividualTarget()).isEqualTo(practitioner);
        assertThat(participantComponent.get().getType().get(0).getText()).isEqualTo("CON");
    }
}
