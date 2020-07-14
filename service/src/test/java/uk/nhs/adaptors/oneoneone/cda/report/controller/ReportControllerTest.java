package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.readAllBytes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.server.ResponseStatusException;

import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {

    private static final String MESSAGE_ID = "2B77B3F5-3016-4A6D-821F-152CE420E58D";
    private static final String TRACKING_ID = "7D6F23E0-AE1A-11DB-9808-B18E1E0994CD";

    @InjectMocks
    private ReportController reportController;

    @Mock
    private EncounterReportService encounterReportService;

    @Test
    public void postReportValidRequest() {
        String validRequest = getValidReportRequest();

        reportController.postReport(validRequest);

        ArgumentCaptor<POCDMT000002UK01ClinicalDocument1> captor = ArgumentCaptor.forClass(POCDMT000002UK01ClinicalDocument1.class);
        verify(encounterReportService).transformAndPopulateToGP(captor.capture(), eq(MESSAGE_ID), eq(TRACKING_ID));
        POCDMT000002UK01ClinicalDocument1 clinicalDocument = captor.getValue();
        assertThat(clinicalDocument.getId().getRoot()).isEqualTo("A709A442-3CF4-476E-8377-376500E829C9");
        assertThat(clinicalDocument.getSetId().getRoot()).isEqualTo("411910CF-1A76-4330-98FE-C345DDEE5553");
    }

    private String getValidReportRequest() {
        try {
            URL reportXmlResource = this.getClass().getResource("/xml/ITK_Report_request.xml");
            return new String(readAllBytes(Paths.get(reportXmlResource.getPath())), defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void postReportInvalidRequest() {
        String invalidRequest = "<invalid>";

        boolean exceptionThrown = false;
        try {
            reportController.postReport(invalidRequest);
        } catch (ResponseStatusException exc) {
            assertThat(exc.getStatus()).isEqualTo(BAD_REQUEST);
            exceptionThrown = true;
        }

        if (!exceptionThrown) {
            fail("ResponseStatusException should have been thrown");
        }
    }
}
