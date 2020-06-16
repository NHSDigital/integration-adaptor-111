package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.nio.file.Files.readAllBytes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import static io.restassured.RestAssured.given;

import java.net.URL;
import java.nio.file.Paths;

import javax.jms.JMSException;
import javax.jms.Message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import uk.nhs.adaptors.oneoneone.config.AmqpProperties;
import uk.nhs.adaptors.oneoneone.utils.FhirJsonValidator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ReportControllerIT {

    @Autowired
    private FhirJsonValidator validator;

    private static final String REPORT_ENDPOINT = "/report";
    @LocalServerPort
    private int port;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private AmqpProperties amqpProperties;

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
    public void postReportValidBody() throws JMSException {
        given()
            .port(port)
            .contentType(APPLICATION_XML_VALUE)
            .body(getResourceAsString("/xml/ITK_Report_request.xml"))
            .when()
            .post(REPORT_ENDPOINT)
            .then()
            .statusCode(ACCEPTED.value());

        Message jmsMessage = jmsTemplate.receive(amqpProperties.getQueueName());
        String messageBody = jmsMessage.getBody(String.class);
        assertThat(validator.isValid(messageBody)).isEqualTo(true);
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
