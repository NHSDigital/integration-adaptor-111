package uk.nhs.adaptors.oneoneone.cda.report.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentException;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.nhs.adaptors.containers.IntegrationTestsExtension;
import uk.nhs.adaptors.oneoneone.config.AmqpProperties;
import uk.nhs.adaptors.oneoneone.utils.FhirJsonValidator;
import uk.nhs.adaptors.oneoneone.utils.ResponseElement;
import uk.nhs.adaptors.oneoneone.utils.ResponseParserUtil;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;
import static uk.nhs.adaptors.oneoneone.utils.ResponseElement.ACTION;
import static uk.nhs.adaptors.oneoneone.utils.ResponseElement.BODY;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@AutoConfigureMockMvc
@DirtiesContext
@Slf4j
public class ReportControllerIT {

    private static final String APPLICATION_XML_UTF_8 = APPLICATION_XML_VALUE+";charset=UTF-8";
    public static final String MESSAGE_ID_VALUE = "2B77B3F5-3016-4A6D-821F-152CE420E58D";
    public static final String MESSAGE_ID = "messageId";
    private static final String REPORT_ENDPOINT = "/report";
    private static final String EXPECTED_ACTION = "urn:nhs-itk:services:201005:SendNHS111Report-v2-0Response";
    private static final String EXPECTED_BODY = "<itk:SimpleMessageResponse xmlns:itk=\"urn:nhs-itk:ns:201005\">OK:%s</itk"
        + ":SimpleMessageResponse>";
    private static final List<String> IGNORED_JSON_PATHS = List.of(
        "entry[*].fullUrl",
        "entry[*].resource.addresses[*].reference",
        "entry[*].resource.appointment.reference",
        "entry[*].resource.author[*].reference",
        "entry[*].resource.authoredOn",
        "entry[*].resource.careManager.reference",
        "entry[*].resource.consentingParty[*].reference",
        "entry[*].resource.context.reference",
        "entry[*].resource.custodian.reference",
        "entry[*].resource.data[*].reference.reference",
        "entry[*].resource.date",
        "entry[*].resource.dateTime",
        "entry[*].resource.encounter.reference",
        "entry[*].resource.entry[*].item.reference",
        "entry[*].resource.episodeOfCare[*].reference",
        "entry[*].resource.evidence[*].detail[*].reference",
        "entry[*].resource.generalPractitioner[*].reference",
        "entry[*].resource.incomingReferral[*].reference",
        "entry[*].resource.location[*].location.reference",
        "entry[*].resource.location[*].reference",
        "entry[*].resource.managingOrganization.reference",
        "entry[*].resource.occurrencePeriod.end",
        "entry[*].resource.occurrencePeriod.start",
        "entry[*].resource.organization[*].reference",
        "entry[*].resource.participant[*].actor.reference",
        "entry[*].resource.participant[*].individual.reference",
        "entry[*].resource.patient.reference",
        "entry[*].resource.providedBy.reference",
        "entry[*].resource.questionnaire.reference",
        "entry[*].resource.reasonReference[*].reference",
        "entry[*].resource.recipient[*].reference",
        "entry[*].resource.requester.onBehalfOf.reference",
        "entry[*].resource.section[*].entry[*].reference",
        "entry[*].resource.serviceProvider.reference",
        "entry[*].resource.subject.reference",
        "entry[*].resource.supportingInfo[*].reference",
        "entry[*].resource.timestamp");

    @Autowired
    private FhirJsonValidator validator;
    @LocalServerPort
    private int port;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private AmqpProperties amqpProperties;

    @Autowired
    private ResponseParserUtil responseParserUtil;

    @Value("classpath:json/expectedResult.json")
    private Resource expectedJson;

    @Value("classpath:xml/ITK_Report_request.xml")
    private Resource itkReportRequest;

    @BeforeAll
    public static void setUp() {
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    @AfterAll
    public static void tearDown() {
        TimeZone.setDefault(null);
    }

    @Test
    public void postReportInvalidBody() {
        given()
            .port(port)
            .contentType(APPLICATION_XML_UTF_8)
            .body("<invalid_xml>")
            .when()
            .post(REPORT_ENDPOINT)
            .then()
            .contentType(TEXT_XML_VALUE)
            .statusCode(INTERNAL_SERVER_ERROR.value())
            .extract();
    }

    @Test
    public void postReportValidBody() throws JMSException, DocumentException, JSONException, IOException {
        String responseBody = given()
            .port(port)
            .contentType(APPLICATION_XML_UTF_8)
            .body(IOUtils.toString(itkReportRequest.getInputStream(), StandardCharsets.UTF_8))
            .when()
            .post(REPORT_ENDPOINT)
            .then()
            .contentType(TEXT_XML_VALUE)
            .statusCode(OK.value())
            .extract()
            .asString();

        Map<ResponseElement, String> responseElementsMap = responseParserUtil.parseSuccessfulResponseXml(responseBody);
        assertThat(responseElementsMap.get(ACTION)).isEqualTo(EXPECTED_ACTION);
        assertThat(responseElementsMap.get(BODY)).isEqualTo(String.format(EXPECTED_BODY, MESSAGE_ID_VALUE));

        Message jmsMessage = jmsTemplate.receive(amqpProperties.getQueueName());
        if (jmsMessage == null) {
            throw new IllegalStateException("Message must not be null");
        }
        String messageBody = jmsMessage.getBody(String.class);

        assertThat(validator.isValid(messageBody)).isEqualTo(true);
        assertThat(jmsMessage.getStringProperty(MESSAGE_ID)).isEqualTo(MESSAGE_ID_VALUE);

        assertMessageContent(messageBody);
    }

    private void assertMessageContent(String actual) throws JSONException, IOException {
        LOGGER.info("Validating message content:\n{}", actual);
        var expected = IOUtils.toString(expectedJson.getInputStream(), StandardCharsets.UTF_8);

        //when comparing json objects, this will ignore various json paths that contain random values like ids or timestamps
        var customizations = IGNORED_JSON_PATHS.stream()
            .map(jsonPath -> new Customization(jsonPath, (o1, o2) -> true))
            .toArray(Customization[]::new);

        JSONAssert.assertEquals(expected, actual,
            new CustomComparator(JSONCompareMode.STRICT, customizations));
    }
}
