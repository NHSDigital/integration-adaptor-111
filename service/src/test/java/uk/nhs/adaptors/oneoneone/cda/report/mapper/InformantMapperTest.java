package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InformantMapperTest {

    @Mock
    private PractitionerMapper practitionerMapper;

    @Mock
    private Practitioner practitioner;

    @Mock
    private ResourceUtil resourceUtil;

    @InjectMocks
    private InformantMapper informantMapper;

    @Test
    public void shouldMapInformantToParticipantComponent() {
        POCDMT000002UK01Informant12 informant = mock(POCDMT000002UK01Informant12.class);
        POCDMT000002UK01AssignedEntity assignedEntity = mock(POCDMT000002UK01AssignedEntity.class);

        when(informant.getTypeCode()).thenReturn("CON");
        when(informant.isSetAssignedEntity()).thenReturn(true);
        when(informant.getAssignedEntity()).thenReturn(assignedEntity);
        when(practitionerMapper.mapPractitioner(isA(POCDMT000002UK01AssignedEntity.class)))
            .thenReturn(practitioner);
        when(resourceUtil.createReference(practitioner)).thenReturn(new Reference(practitioner));

        Optional<Encounter.EncounterParticipantComponent> participantComponent = informantMapper
            .mapInformantIntoParticipantComponent(informant);

        assertThat(participantComponent.isPresent()).isTrue();
        assertThat(participantComponent.get().getIndividualTarget()).isEqualTo(practitioner);
        assertThat(participantComponent.get().getType().get(0).getText()).isEqualTo("CON");
    }
}
