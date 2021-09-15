package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static io.restassured.RestAssured.given;
import static uk.nhs.adaptors.TestResourceUtils.readResourceAsString;
import static uk.nhs.adaptors.oneoneone.utils.ResponseElement.ACTION;
import static uk.nhs.adaptors.oneoneone.utils.ResponseElement.BODY;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

import javax.jms.IllegalStateException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.xml.sax.SAXException;

import com.github.tomakehurst.wiremock.WireMockServer;

import junitparams.JUnitParamsRunner;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.WireMockInitializer;
import uk.nhs.adaptors.containers.IntegrationTestsExtension;
import uk.nhs.adaptors.oneoneone.config.AmqpProperties;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;
import uk.nhs.adaptors.oneoneone.utils.FhirJsonValidator;
import uk.nhs.adaptors.oneoneone.utils.ResponseElement;
import uk.nhs.adaptors.oneoneone.utils.ResponseParserUtil;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith({SpringExtension.class, IntegrationTestsExtension.class})
@AutoConfigureMockMvc
@DirtiesContext
@Slf4j
@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(initializers = WireMockInitializer.class)
public class ReportControllerIT {

    private static final String APPLICATION_XML_UTF_8 = APPLICATION_XML_VALUE + ";charset=UTF-8";
    private static final boolean OVERWRITE_JSON = false;
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
        "entry[*].resource.id",
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
        "entry[*].resource.requester.agent.reference",
        "entry[*].resource.requester.onBehalfOf.reference",
        "entry[*].resource.section[*].entry[*].reference",
        "entry[*].resource.serviceProvider.reference",
        "entry[*].resource.source.reference",
        "entry[*].resource.subject.reference",
        "entry[*].resource.supportingInfo[*].reference",
        "entry[*].resource.practitioner.reference",
        "entry[*].resource.organization.reference");

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

    @Autowired
    private WireMockServer wireMockServer;

    @Autowired
    private ItkProperties itkProperties;

    private static Stream<Arguments> validItkReportAndExpectedJsonValues() {
        return Stream.of(
            Arguments.of(
                "/xml/primary-emergency-itk-request.xml",
                "/json/primary-emergency-fhir-result.json",
                "2B77B3F5-3016-4A6D-821F-152CE420E58D"
            ),
            Arguments.of(
                "/xml/repeat-caller-itk-request.xml",
                "/json/repeat-caller-fhir-result.json",
                "2B77B3F5-3016-4A6D-821F-152CE420E58D"
            ),
            Arguments.of(
                "/xml/adastra-itk-ooh-referral-dx06-itk-request.xml",
                "/json/adastra-itk-ooh-referral-dx06-fhir-result.json",
                "7AEFFED7-78AF-4940-9EE2-5FAA3920ECFE"
            ),
            Arguments.of(
                "/xml/adastra-itk-ooh-referral-dx86-itk-request.xml",
                "/json/adastra-itk-ooh-referral-dx86-fhir-result.json",
                "2F182787-CA39-46F8-9B1C-8E77F3050067"
            ),
            Arguments.of(
                "/xml/cleric-itk-ooh-referral-dx09-itk-request.xml",
                "/json/cleric-itk-ooh-referral-dx09-fhir-result.json",
                "C34D38AA-2A35-4E0C-AAB2-BCC8D397736A"
            ),
            Arguments.of(
                "/xml/cleric-itk-ooh-referral-dx10-itk-request.xml",
                "/json/cleric-itk-ooh-referral-dx10-fhir-result.json",
                "B447241A-CEBD-41DF-AC62-46FE8022876F"
            ),
            Arguments.of(
                "/xml/conformance-example-adastra-ooh-itk-request.xml",
                "/json/conformance-example-adastra-ooh-fhir-result.json",
                "05040617-33BA-4344-AA59-281CF2FE63CA"
            ),
            Arguments.of(
                "/xml/conformance-example-adastra-original-itk-request.xml",
                "/json/conformance-example-adastra-original-fhir-result.json",
                "F7916D36-4D5F-4A64-BD08-644E8A234AE2"
            ),
            Arguments.of(
                "/xml/conformance-example-ic24-original-itk-request.xml",
                "/json/conformance-example-ic24-original-fhir-result.json",
                "F7916D36-4D5F-4A64-BD08-644E8A234AE2"
            ),
            Arguments.of(
                "/xml/example-adastra-ooh-itk-request.xml",
                "/json/example-adastra-ooh-fhir-result.json",
                "05040617-33BA-4344-AA59-281CF2FE63CA"
            ),
            Arguments.of(
                "/xml/ic24-itk-ooh-referral-dx06-itk-request.xml",
                "/json/ic24-itk-ooh-referral-dx06-fhir-result.json",
                "D78E4686-5082-4E76-9359-6A84AE0C9C10"
            ),
            Arguments.of(
                "/xml/ic24-itk-ooh-referral-dx07-itk-request.xml",
                "/json/ic24-itk-ooh-referral-dx07-fhir-result.json",
                "4A4DE551-F585-49BB-A1D3-12DCC764AF52"
            ),
            Arguments.of(
                "/xml/repeat-caller-exampleV1-itk-request.xml",
                "/json/repeat-caller-exampleV1-fhir-result.json",
                "A12F527F-3808-44BC-9272-8C8A92884A37"
            )
        );
    }

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

    @ParameterizedTest(name = "postReportValidBody")
    @MethodSource("validItkReportAndExpectedJsonValues")
    public void postReportValidBody(String itkReportRequestPath, String expectedJsonPath, String messageIdValue)
        throws JMSException, JSONException, ParserConfigurationException, SAXException, IOException {
        String responseBody = given()
            .port(port)
            .contentType(APPLICATION_XML_UTF_8)
            .body(readResourceAsString(itkReportRequestPath))
            .when()
            .post(REPORT_ENDPOINT)
            .then()
            .contentType(TEXT_XML_VALUE)
            .statusCode(OK.value())
            .extract()
            .asString();

        Map<ResponseElement, String> responseElementsMap = responseParserUtil.parseSuccessfulResponseXml(responseBody);
        assertThat(responseElementsMap.get(ACTION)).isEqualTo(EXPECTED_ACTION);
        assertThat(responseElementsMap.get(BODY)).isEqualTo(String.format(EXPECTED_BODY, messageIdValue));

        Message jmsMessage = jmsTemplate.receive(amqpProperties.getQueueName());
        if (jmsMessage == null) {
            throw new IllegalStateException("Message must not be null");
        }
        String messageBody = jmsMessage.getBody(String.class);
        if (OVERWRITE_JSON) {
            try (PrintWriter printWriter = new PrintWriter("../doc" + expectedJsonPath, StandardCharsets.UTF_8)) {
                printWriter.print(messageBody);
            }
            fail("Re-run the tests with OVERWRITE_JSON=false");
        }

        assertThat(validator.isValid(messageBody)).isEqualTo(true);
        assertThat(jmsMessage.getStringProperty(MESSAGE_ID)).isEqualTo(messageIdValue);

        assertMessageContent(messageBody, readResourceAsString(expectedJsonPath));
    }

    private void assertMessageContent(String actual, String expected) throws JSONException {
        LOGGER.info("Validating message content:\n{}", actual);

        //when comparing json objects, this will ignore various json paths that contain random values like ids or timestamps
        var customizations = IGNORED_JSON_PATHS.stream()
            .map(jsonPath -> new Customization(jsonPath, (o1, o2) -> true))
            .toArray(Customization[]::new);

        JSONAssert.assertEquals(expected, actual,
            new CustomComparator(JSONCompareMode.STRICT, customizations));
    }
}
