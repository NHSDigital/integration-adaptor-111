package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssociatedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Informant12;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedEntity;

@ExtendWith(MockitoExtension.class)
public class ParticipantMapperTest {
    private static final String RELATED_PERSON_ID = "123456789abc";

    @Mock
    private PractitionerMapper practitionerMapper;

    @Mock
    private PeriodMapper periodMapper;

    @Mock
    private Practitioner practitioner;

    @Mock
    private Period period;

    @Mock
    private RelatedPersonMapper relatedPersonMapper;

    @InjectMocks
    private ParticipantMapper participantMapper;

    @Test
    public void shouldMapParticipant() {
        POCDMT000002UK01Participant1 encounterParticipant = mock(POCDMT000002UK01Participant1.class);
        POCDMT000002UK01AssociatedEntity associatedEntity = mock(POCDMT000002UK01AssociatedEntity.class);
        IVLTS time = mock(IVLTS.class);

        when(encounterParticipant.getTypeCode()).thenReturn("CON");
        when(encounterParticipant.getAssociatedEntity()).thenReturn(associatedEntity);
        when(encounterParticipant.isSetTime()).thenReturn(true);
        when(encounterParticipant.getTime()).thenReturn(time);
        when(periodMapper.mapPeriod(ArgumentMatchers.isA(IVLTS.class)))
            .thenReturn(period);
        when(practitionerMapper.mapPractitioner(ArgumentMatchers.isA(POCDMT000002UK01AssociatedEntity.class)))
            .thenReturn(practitioner);

        Encounter.EncounterParticipantComponent participantComponent = participantMapper.mapEncounterParticipant(encounterParticipant);

        assertThat(participantComponent.getIndividualTarget()).isEqualTo(practitioner);
        assertThat(participantComponent.getPeriod()).isEqualTo(period);
        assertThat(participantComponent.getType().get(0).getText()).isEqualTo("CON");
    }

    @Test
    public void shouldMapEncounterRelatedPerson() {
        IVLTS time = IVLTS.Factory.newInstance();
        POCDMT000002UK01RelatedEntity relatedEntity = POCDMT000002UK01RelatedEntity.Factory.newInstance();
        relatedEntity.setEffectiveTime(time);
        POCDMT000002UK01Informant12 informant = POCDMT000002UK01Informant12.Factory.newInstance();
        informant.setTypeCode("INF");
        informant.setRelatedEntity(relatedEntity);
        Encounter encounter = new Encounter();
        RelatedPerson relatedPerson = new RelatedPerson();
        relatedPerson.setId(RELATED_PERSON_ID);
        when(relatedPersonMapper.mapRelatedPerson(informant, encounter)).thenReturn(relatedPerson);
        when(periodMapper.mapPeriod(ArgumentMatchers.isA(IVLTS.class))).thenReturn(period);

        Encounter.EncounterParticipantComponent participantComponent = participantMapper.mapEncounterRelatedPerson(informant, encounter);

        assertThat(participantComponent.getType().get(0).getText()).isEqualTo("Informant");
        assertThat(participantComponent.getIndividualTarget().getId()).isEqualTo(RELATED_PERSON_ID);
        assertThat(participantComponent.getPeriod()).isEqualTo(period);
    }
}
