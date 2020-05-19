package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.nio.file.Files.readAllBytes;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

import static io.restassured.RestAssured.given;

import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ReportControllerIT {

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
            .body(getValidReportRequest())
            .when()
            .post(REPORT_ENDPOINT)
            .then()
            .statusCode(ACCEPTED.value());

        //TODO
        //Verify if message actually made its way to the queue
    }

    private String getValidReportRequest() {
        try {
            URL reportXmlResource = this.getClass().getResource("/xml/ITK_Report_request.xml");
            return new String(readAllBytes(Paths.get(reportXmlResource.getPath())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
