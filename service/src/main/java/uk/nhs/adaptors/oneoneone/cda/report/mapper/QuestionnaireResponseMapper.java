package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine.Question;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine.Question.Answers.Answer;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;

@Component
@AllArgsConstructor
public class QuestionnaireResponseMapper {

    private static final String NOT_APPLICABLE = "N/A";
    private final QuestionnaireMapper questionnaireMapper;
    private final ResourceUtil resourceUtil;

    public QuestionnaireResponse mapQuestionnaireResponse(PathwaysCase pathwaysCase, Reference patient, Reference encounter,
        TriageLine triageLine) {
        QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        Questionnaire questionnaire = questionnaireMapper.mapQuestionnaire(pathwaysCase, triageLine);

        questionnaireResponse.setIdElement(resourceUtil.newRandomUuid());

        questionnaireResponse
            .setQuestionnaire(resourceUtil.createReference(questionnaire))
            .setQuestionnaireTarget(questionnaire)
            .setStatus(QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED)
            .setSubject(patient)
            .setContext(encounter);

        if (pathwaysCase.getCaseDetails() != null) {
            if (pathwaysCase.getCaseDetails().isSetCaseId()) {
                questionnaireResponse.setIdentifier(new Identifier().setValue(pathwaysCase.getCaseDetails().getCaseId()));
            }
        }
        if (pathwaysCase.isSetCaseReceiveEnd()) {
            questionnaireResponse.setAuthored(DateUtil.parsePathwaysDate(pathwaysCase.getCaseReceiveEnd().toString()));
        }
        if (triageLine.getQuestion() != null) {
            questionnaireResponse.addItem(getItem(triageLine.getQuestion()));
        }

        return questionnaireResponse;
    }

    private QuestionnaireResponseItemComponent getItem(Question question) {
        QuestionnaireResponseItemComponent item = new QuestionnaireResponseItemComponent();
        QuestionnaireResponseItemAnswerComponent answer = new QuestionnaireResponseItemAnswerComponent();
        StringType correctAnswerText = new StringType();

        if (StringUtils.isBlank(question.getQuestionId())) {
            item.setLinkId(NOT_APPLICABLE);
        } else {
            item.setLinkId(question.getQuestionId());
        }

        item.setText(question.getQuestionText());

        for (Answer singleAnswer : question.getAnswers().getAnswerArray()) {
            if (singleAnswer.getSelected() && !singleAnswer.getText().isBlank()) {
                correctAnswerText.setValueAsString(singleAnswer.getText());
            }
        }

        answer.setValue(correctAnswerText);
        item.addAnswer(answer);

        return item;
    }
}
