package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.nio.file.Files.readAllBytes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static io.restassured.RestAssured.given;
import static uk.nhs.adaptors.oneoneone.utils.ResponseElement.ACTION;
import static uk.nhs.adaptors.oneoneone.utils.ResponseElement.BODY;

import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import uk.nhs.adaptors.oneoneone.config.AmqpProperties;
import uk.nhs.adaptors.oneoneone.utils.FhirJsonValidator;
import uk.nhs.adaptors.oneoneone.utils.ResponseElement;
import uk.nhs.adaptors.oneoneone.utils.ResponseParserUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ReportControllerIT {

    public static final String MESSAGE_ID_VALUE = "2B77B3F5-3016-4A6D-821F-152CE420E58D";
    public static final String MESSAGE_ID = "messageId";
    private static final String REPORT_ENDPOINT = "/report";
    private static final String EXPECTED_ACTION = "urn:nhs-itk:services:201005:SendNHS111Report-v2-0Response";
    private static final String EXPECTED_BODY = "<itk:SimpleMessageResponse xmlns:itk=\"urn:nhs-itk:ns:201005\">OK:%s</itk"
        + ":SimpleMessageResponse>";
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

    @Test
    public void postReportInvalidBody() {
        given()
            .port(port)
            .contentType(APPLICATION_XML_VALUE)
            .body("<invalid_xml>")
            .when()
            .post(REPORT_ENDPOINT)
            .then()
            .statusCode(BAD_REQUEST.value()).extract();
    }

    @Test
    public void postReportValidBody() throws JMSException, DocumentException {
        String responseBody = given()
            .port(port)
            .contentType(APPLICATION_XML_VALUE)
            .body(getResourceAsString("/xml/ITK_Report_request.xml"))
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
        String messageBody = jmsMessage.getBody(String.class);

        assertThat(validator.isValid(messageBody)).isEqualTo(true);
        assertThat(jmsMessage.getStringProperty(MESSAGE_ID)).isEqualTo(MESSAGE_ID_VALUE);
    }

    private String getResourceAsString(String path) {
        try {
            URL reportXmlResource = this.getClass().getResource(path);
            return new String(readAllBytes(Paths.get(reportXmlResource.getPath())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
