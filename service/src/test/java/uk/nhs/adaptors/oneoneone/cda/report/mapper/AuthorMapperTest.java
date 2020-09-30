package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedAuthor;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.TS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorMapperTest {

    @Mock
    private PractitionerMapper practitionerMapper;

    @Mock
    private PeriodMapper periodMapper;

    @InjectMocks
    private AuthorMapper authorMapper;

    @Mock
    private Practitioner practitioner;

    @Mock
    private Period period;

    @Test
    public void shouldMapAuthorToParticipantComponent() {
        POCDMT000002UK01Author author = mock(POCDMT000002UK01Author.class);
        POCDMT000002UK01AssignedAuthor assignedAuthor = mock(POCDMT000002UK01AssignedAuthor.class);
        TS time = mock(TS.class);

        when(author.getAssignedAuthor()).thenReturn(assignedAuthor);
        when(author.getTime()).thenReturn(time);
        when(author.getTypeCode()).thenReturn("CON");
        when(practitionerMapper.mapPractitioner(isA(POCDMT000002UK01AssignedAuthor.class)))
            .thenReturn(practitioner);
        when(periodMapper.mapPeriod(isA(TS.class))).thenReturn(period);

        Encounter.EncounterParticipantComponent participantComponent = authorMapper.mapAuthorIntoParticipantComponent(author);

        assertThat(participantComponent.getIndividualTarget()).isEqualTo(practitioner);
        assertThat(participantComponent.getPeriod()).isEqualTo(period);
        assertThat(participantComponent.getType().get(0).getText()).isEqualTo("CON");
    }
}
