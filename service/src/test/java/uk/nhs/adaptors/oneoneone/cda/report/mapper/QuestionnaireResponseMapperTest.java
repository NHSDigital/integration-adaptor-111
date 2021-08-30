package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.util.IsoDateTimeFormatter.toIsoDateTimeString;

import java.util.Calendar;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine;

import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireResponseMapperTest {

    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";

    @InjectMocks
    private QuestionnaireResponseMapper questionnaireResponseMapper;
    @Mock
    private QuestionnaireMapper questionnaireMapper;
    @Mock
    private Reference patient;
    @Mock
    private Reference encounter;
    @Mock
    private TriageLine triageLine;
    @Mock
    private PathwaysCase pathwaysCase;
    @Mock
    private PathwaysCase.CaseDetails caseDetails;
    @Mock
    private Calendar calendar;
    @Mock
    private TriageLine.Question question;
    @Mock
    private TriageLine.Question.Answers answers;

    @Mock
    private TriageLine.Question.Answers.Answer answer;

    @Mock
    private ResourceUtil resourceUtil;

    @Test
    public void shouldMapQuestionnaireResponseFromPathways() {
        TriageLine.Question.Answers.Answer[] answerArray = new TriageLine.Question.Answers.Answer[] {answer};
        String caseId = "caseId";
        String questionText = "this is my question";
        String answerText = "this is my answer";
        when(pathwaysCase.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getCaseId()).thenReturn(caseId);
        when(caseDetails.isSetCaseId()).thenReturn(true);
        when(pathwaysCase.isSetCaseReceiveEnd()).thenReturn(true);
        when(pathwaysCase.getCaseReceiveEnd()).thenReturn(calendar);
        when(calendar.toString()).thenReturn("2011-02-17T17:31:14.313Z");
        when(triageLine.getQuestion()).thenReturn(question);
        when(question.getQuestionText()).thenReturn(questionText);
        when(question.getAnswers()).thenReturn(answers);
        when(answers.getAnswerArray()).thenReturn(answerArray);
        when(answer.getSelected()).thenReturn(true);
        when(answer.getText()).thenReturn(answerText);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));

        QuestionnaireResponse questionnaireResponse = questionnaireResponseMapper.mapQuestionnaireResponse(pathwaysCase, patient,
            encounter, triageLine);
        assertThat(questionnaireResponse.getStatus()).isEqualTo(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED);
        assertThat(questionnaireResponse.getSubject()).isEqualTo(patient);
        assertThat(questionnaireResponse.getContext()).isEqualTo(encounter);
        assertThat(questionnaireResponse.getIdentifier().getValue()).isEqualTo(caseId);
        assertThat(toIsoDateTimeString(questionnaireResponse.getAuthored())).isEqualTo("2011-02-17T17:31:14.313Z");
        assertThat(questionnaireResponse.getItemFirstRep().getText()).isEqualTo(questionText);
        assertThat(questionnaireResponse.getItemFirstRep().getAnswer().get(0).getValue().toString()).isEqualTo(answerText);
    }
}
