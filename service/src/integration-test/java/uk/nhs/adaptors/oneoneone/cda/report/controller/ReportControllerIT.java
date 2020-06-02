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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import uk.nhs.adaptors.oneoneone.utils.FhirJsonValidator;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ReportControllerIT {

    @Autowired
    private FhirJsonValidator validator;

    private static final String REPORT_ENDPOINT = "/report";
    @LocalServerPort
    private int port;

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
    public void postReportValidBody() {
        given()
            .port(port)
            .contentType(APPLICATION_XML_VALUE)
            .body(getResourceAsString("/xml/ITK_Report_request.xml"))
            .when()
            .post(REPORT_ENDPOINT)
            .then()
            .statusCode(ACCEPTED.value());

        //TODO read message from queue instead of hardcoded resource
        String message = getResourceAsString("/fhir/encounter_valid.json");
        assertThat(validator.isValid(message)).isEqualTo(true);
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
