package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem.PHONE;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.util.IsoDateTimeFormatter.toIsoDateTimeString;

import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.CaseDetails;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.CaseDetails.ContactDetails.Caller;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.TriageLineDetails.TriageLine;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument.PathwaysCase.PathwayDetails.PathwayTriageDetails.PathwayTriage.User;

import uk.nhs.adaptors.oneoneone.cda.report.util.PathwayUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireMapperTest {

    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";
    private static final String CASE_ID = "caseId";
    private static final String ORDER_NUMBER = "25";
    private static final String PHONE_NUMBER = "123 456 789";
    private static final String DATE = "2011-02-17T17:31:14.313Z";

    @InjectMocks
    private QuestionnaireMapper questionnaireMapper;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private PathwaysCase pathwaysCase;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private TriageLine triageLine;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private CaseDetails caseDetails;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private User user;
    @Mock(answer = RETURNS_DEEP_STUBS)
    private Caller caller;
    @Mock
    private ResourceUtil resourceUtil;
    @Mock
    private PathwayUtil pathwayUtil;

    @BeforeEach
    public void setUpMocks() {
        when(pathwaysCase.getCaseDetails()).thenReturn(caseDetails);
        when(caseDetails.getCaseId()).thenReturn(CASE_ID);
        when(pathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0).getUser()).thenReturn(user);
        when(pathwaysCase.getCaseReceiveEnd().toString()).thenReturn(DATE);
        when(pathwayUtil.isSetCaseReceiveEnd(pathwaysCase)).thenReturn(true);
        when(triageLine.getQuestion().getTriageLogicId().getPathwayOrderNo()).thenReturn(ORDER_NUMBER);
        when(caseDetails.isSetCaseId()).thenReturn(true);
        when(caseDetails.getContactDetails().getCallerArray())
            .thenReturn(new Caller[] {caller});
        when(caller.getPhone().getNumber()).thenReturn(PHONE_NUMBER);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
    }

    @Test
    public void shouldMapQuestionnaireResponseFromPathways() {
        when(pathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0).getUser()).thenReturn(user);

        Questionnaire questionnaire = questionnaireMapper.mapQuestionnaire(pathwaysCase, triageLine);

        assertThat(questionnaire.getIdentifier().get(0).getValue()).isEqualTo(CASE_ID);
        assertThat(questionnaire.getSubjectType().get(0).getValue()).isEqualTo("Patient");
        assertThat(questionnaire.getExperimental()).isEqualTo(false);
        assertThat(questionnaire.getStatus()).isEqualTo(Enumerations.PublicationStatus.ACTIVE);
        assertThat(toIsoDateTimeString(questionnaire.getDate())).isEqualTo(DATE);
        assertThat(questionnaire.getPublisher()).isEqualTo("N/A");
        assertThat(questionnaire.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(questionnaire.getContactFirstRep().getTelecomFirstRep().getValue()).isEqualTo(PHONE_NUMBER);
        assertThat(questionnaire.getContactFirstRep().getTelecomFirstRep().getSystem()).isEqualTo(PHONE);
        assertThat(questionnaire.getItem().get(0).getPrefix()).isEqualTo(ORDER_NUMBER);
    }

    @Test
    public void shouldMapQuestionnairePublisherId() {
        var userId = "123456";
        when(pathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0).getUser().getId()).thenReturn(userId);

        Questionnaire questionnaire = questionnaireMapper.mapQuestionnaire(pathwaysCase, triageLine);

        assertThat(questionnaire.getPublisher()).isEqualTo(String.format("User ID: '%s' ", userId));
    }

    @Test
    public void shouldMapQuestionnairePublisherName() {
        var userName = "John";
        when(pathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0).getUser().getName()).thenReturn(userName);

        Questionnaire questionnaire = questionnaireMapper.mapQuestionnaire(pathwaysCase, triageLine);

        assertThat(questionnaire.getPublisher()).isEqualTo(String.format("User name: '%s' ", userName));
    }

    @Test
    public void shouldMapQuestionnairePublisherSkillSet() {
        when(pathwaysCase.getPathwayDetails().getPathwayTriageDetails().getPathwayTriageArray(0).getUser().getSkillSet())
            .thenThrow(XmlValueOutOfRangeException.class);

        Questionnaire questionnaire = questionnaireMapper.mapQuestionnaire(pathwaysCase, triageLine);

        assertThat(questionnaire.getPublisher()).isEqualTo("User skill set: 'Unknown'");
    }
}
