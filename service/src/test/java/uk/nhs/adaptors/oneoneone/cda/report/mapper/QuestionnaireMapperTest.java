package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.PHONE;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.util.IsoDateTimeFormatter.toIsoDateTimeString;

import java.util.Calendar;

import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine;

import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireMapperTest {

    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";

    @InjectMocks
    private QuestionnaireMapper questionnaireMapper;
    @Mock
    private PathwaysCase pathwaysCase;
    @Mock
    private TriageLine triageLine;
    @Mock
    private PathwaysCase.CaseDetails caseDetails;
    @Mock
    private PathwaysCase.PathwayDetails pathwayDetails;
    @Mock
    private PathwaysCase.PathwayDetails.PathwayTriageDetails pathwayTriageDetails;
    @Mock
    private PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage pathwayTriage;
    @Mock
    private PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.User user;
    @Mock
    private Calendar calendar;
    @Mock
    private TriageLine.Question question;
    @Mock
    private TriageLine.Question.TriageLogicId triageLogicId;
    @Mock
    private ResourceUtil resourceUtil;

    @Test
    public void shouldMapQuestionnaireResponseFromPathways() {
        String caseId = "caseId";
        String orderNumber = "25";

        when(pathwaysCase.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getCaseId()).thenReturn(caseId);
        when(pathwaysCase.getPathwayDetails()).thenReturn(pathwayDetails);
        when(pathwayDetails.getPathwayTriageDetails()).thenReturn(pathwayTriageDetails);
        when(pathwayTriageDetails.getPathwayTriageArray(0)).thenReturn(pathwayTriage);
        when(pathwayTriage.getUser()).thenReturn(user);
        when(pathwaysCase.getCaseReceiveEnd()).thenReturn(calendar);
        when(calendar.toString()).thenReturn("2011-02-17T17:31:14.313Z");
        when(pathwaysCase.isSetCaseReceiveEnd()).thenReturn(true);
        when(triageLine.getQuestion()).thenReturn(question);
        when(question.getTriageLogicId()).thenReturn(triageLogicId);
        when(triageLogicId.getPathwayOrderNo()).thenReturn(orderNumber);
        when(caseDetails.isSetCaseId()).thenReturn(true);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        Questionnaire questionnaire = questionnaireMapper.mapQuestionnaire(pathwaysCase, triageLine);

        assertThat(questionnaire.getIdentifier().get(0).getValue()).isEqualTo(caseId);
        assertThat(questionnaire.getSubjectType().get(0).getValue()).isEqualTo("Patient");
        assertThat(questionnaire.getExperimental()).isEqualTo(false);
        assertThat(questionnaire.getStatus()).isEqualTo(Enumerations.PublicationStatus.ACTIVE);
        assertThat(toIsoDateTimeString(questionnaire.getDate())).isEqualTo("2011-02-17T17:31:14.313Z");
        assertThat(questionnaire.getPublisher()).isEqualTo("N/A");
        assertThat(questionnaire.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat((questionnaire.getContact().get(0).getTelecom().get(0).getSystem())).isEqualTo(PHONE);
    }
}
