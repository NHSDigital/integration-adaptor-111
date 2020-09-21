package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactDetail;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine.Question;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine.Question.Answers.Answer;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.User;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;

@Component
@AllArgsConstructor
public class QuestionnaireMapper {
    public Questionnaire mapQuestionnaire(PathwaysCase pathwaysCase, TriageLine triageLine) {
        Questionnaire questionnaire = new Questionnaire();
        String publisher = getPublisher(pathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0).getUser());
        Date latestDate = getLatestDate(pathwaysCase);

        questionnaire.addIdentifier(new Identifier().setValue(getCaseID(pathwaysCase)));
        questionnaire.setVersion(latestDate.toString());
        questionnaire.setStatus(Enumerations.PublicationStatus.ACTIVE);
        questionnaire.setExperimental(false);
        questionnaire.addSubjectType("Patient");
        questionnaire.setDate(latestDate);
        questionnaire.setPublisher(publisher);
        questionnaire.setLastReviewDate(latestDate);
        questionnaire.setJurisdiction(Collections.singletonList(new CodeableConcept().setText(getCountry(pathwaysCase))));
        questionnaire.addContact(
            new ContactDetail().addTelecom(
                new ContactPoint().setValue(
                    getContactNumber(pathwaysCase)
                )));

        questionnaire.addItem(getItem(triageLine.getQuestion(), getCaseID(pathwaysCase)));

        return questionnaire;
    }

    private String getPublisher(User user) {
        String value = "";
        if (user.getId() != null) {
            value += String.format("User ID: '%s' ", user.getId());
        }
        if (user.getName() != null) {
            value += String.format("User name: '%s' ", user.getName());
        }
        if (user.getSkillSet() != null) {
            value += String.format("User skill set: '%s'", user.getSkillSet());
        }
        if (value.equals("")) {
            return "N/A";
        }

        return value;
    }

    private Date getLatestDate(PathwaysCase pathwaysCase) {
        if (pathwaysCase.isSetCaseReceiveEnd()) {
            return DateUtil.parsePathwaysDate(pathwaysCase.getCaseReceiveEnd().toString());
        }

        return null;
    }

    private String getCaseID(PathwaysCase pathwaysCase) {
        if (pathwaysCase.getCaseDetails() != null) {
            if (pathwaysCase.getCaseDetails().isSetCaseId()) {
                return pathwaysCase.getCaseDetails().getCaseId();
            }
        }

        return null;
    }

    private String getCountry(PathwaysCase pathwaysCase) {
        if (pathwaysCase.getCaseDetails() != null) {
            if (pathwaysCase.getCaseDetails().isSetAddress()) {
                if (pathwaysCase.getCaseDetails().getAddress().isSetCountry()) {
                    if (pathwaysCase.getCaseDetails().getAddress().getCountry().getName() != null) {
                        return pathwaysCase.getCaseDetails().getAddress().getCountry().getName();
                    }
                }
            }
        }

        return null;
    }

    private String getContactNumber(PathwaysCase pathwaysCase) {
        if (pathwaysCase.getCaseDetails() != null) {
            if (pathwaysCase.getCaseDetails().getContactDetails() != null) {
                if (pathwaysCase.getCaseDetails().getContactDetails().sizeOfCallerArray() > 0) {
                    if (pathwaysCase.getCaseDetails().getContactDetails().getCallerArray(0).isSetPhone()) {
                        if (pathwaysCase.getCaseDetails().getContactDetails().getCallerArray(0).getPhone().getNumber() != null) {
                            return pathwaysCase.getCaseDetails().getContactDetails().getCallerArray(0).getPhone().getNumber();
                        }
                    }
                }
            }
        }

        return null;
    }

    private QuestionnaireItemComponent getItem(Question question, String caseId) {
        List<QuestionnaireItemOptionComponent> questionnaireItemOptionComponentList = new ArrayList<>();
        QuestionnaireItemComponent item = new QuestionnaireItemComponent();

        item.setLinkId(caseId);
        item.setPrefix(getPrefix(question));
        item.setType(Questionnaire.QuestionnaireItemType.CHOICE);
        item.setRequired(true);
        item.setRepeats(false);

        if (question.getQuestionText() != null) {
            item.setText(question.getQuestionText());
        } else {
            item.setText("N/A");
        }

        if (question.getAnswers() != null) {
            if (question.getAnswers().sizeOfAnswerArray() > 0) {
                for (Answer answer : question.getAnswers().getAnswerArray()) {
                    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent();
                    StringType answerStringType = new StringType();
                    answerStringType.setValueAsString(String.format("%s, Selected: %s", answer.getText(), answer.getSelected()));
                    optionComponent.setValue(answerStringType);
                    questionnaireItemOptionComponentList.add(optionComponent);
                }
            }
        }

        item.setOption(questionnaireItemOptionComponentList);

        return item;
    }

    private String getPrefix(Question question) {
        if (question.getTriageLogicId() != null) {
            if (question.getTriageLogicId().getPathwayOrderNo() != null) {
                return question.getTriageLogicId().getPathwayOrderNo();
            }
        }
        return null;
    }
}
