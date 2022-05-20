package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.PHONE;

import static uk.nhs.adaptors.oneoneone.cda.report.util.IsoDateTimeFormatter.toIsoDateTimeString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.ContactDetail;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine.Question;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine.Question.Answers.Answer;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.User;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.User.SkillSet;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.PathwayUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;

@Component
@AllArgsConstructor
public class QuestionnaireMapper {
    private static final String NOT_APPLICABLE = "N/A";
    private final ResourceUtil resourceUtil;
    private final PathwayUtil pathwayUtil;

    public Questionnaire mapQuestionnaire(PathwaysCase pathwaysCase, TriageLine triageLine) {
        Questionnaire questionnaire = new Questionnaire();
        String publisher = getPublisher(pathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0).getUser());
        Optional<Date> latestDateOptional = getLatestDate(pathwaysCase);

        questionnaire.setIdElement(resourceUtil.newRandomUuid());
        questionnaire.addIdentifier(new Identifier().setValue(getCaseID(pathwaysCase)))
            .setStatus(Enumerations.PublicationStatus.ACTIVE)
            .setExperimental(false)
            .addSubjectType("Patient")
            .setPublisher(publisher)
            .setJurisdiction(Collections.singletonList(
                new CodeableConcept().setText(
                    getCountry(pathwaysCase))))
            .addItem(getItem(triageLine.getQuestion(), getCaseID(pathwaysCase)));
        setContact(pathwaysCase, questionnaire);

        latestDateOptional.ifPresent(latestDate -> {
            questionnaire.setVersion(toIsoDateTimeString(latestDate))
                .setDate(latestDate)
                .setLastReviewDate(latestDate);
        });

        return questionnaire;
    }

    private void setContact(PathwaysCase pathwaysCase, Questionnaire questionnaire) {
        String contactNumber = getContactNumber(pathwaysCase);
        if (contactNumber != null) {
            questionnaire.addContact(
                new ContactDetail().addTelecom(
                    new ContactPoint()
                        .setSystem(PHONE)
                        .setValue(contactNumber)));
        }
    }

    private String getPublisher(User user) {
        String value = StringUtils.EMPTY;
        if (user.getId() != null) {
            value += String.format("User ID: '%s' ", user.getId());
        }
        if (user.getName() != null) {
            value += String.format("User name: '%s' ", user.getName());
        }
        String skillSet = extractUserSkillSet(user);
        if (skillSet != null) {
            value += String.format("User skill set: '%s'", skillSet);
        }
        if (value.isBlank()) {
            return NOT_APPLICABLE;
        }

        return value;
    }

    private String extractUserSkillSet(User user) {
        try {
            SkillSet.Enum skillSet = user.getSkillSet();
            return skillSet != null ? skillSet.toString() : null;
        } catch (XmlValueOutOfRangeException exc) {
            return "Unknown";
        }
    }

    private Optional<Date> getLatestDate(PathwaysCase pathwaysCase) {
        if (pathwayUtil.isSetCaseReceiveEnd(pathwaysCase)) {
            return Optional.of(DateUtil.parsePathwaysDate(pathwaysCase.getCaseReceiveEnd().toString()));
        }

        return Optional.empty();
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
        return Optional.ofNullable(pathwaysCase.getCaseDetails())
            .map(PathwaysCaseDocument.PathwaysCase.CaseDetails::getContactDetails)
            .map(PathwaysCase.CaseDetails.ContactDetails::getCallerArray)
            .map(it -> it.length > 0 ? it[0] : null)
            .map(PathwaysCaseDocument.PathwaysCase.CaseDetails.ContactDetails.Caller::getPhone)
            .map(PathwaysCaseDocument.PathwaysCase.CaseDetails.ContactDetails.Caller.Phone::getNumber)
            .orElse(null);
    }

    private QuestionnaireItemComponent getItem(Question question, String caseId) {
        if (question != null) {
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
                item.setText(NOT_APPLICABLE);
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
        return null;
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
