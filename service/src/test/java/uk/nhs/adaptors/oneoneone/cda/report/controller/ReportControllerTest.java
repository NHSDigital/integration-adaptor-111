package uk.nhs.adaptors.oneoneone.cda.report.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkResponseUtil;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.adaptors.oneoneone.cda.report.validation.ItkValidator;
import uk.nhs.adaptors.oneoneone.cda.report.validation.SoapValidator;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.net.URL;
import java.nio.file.Paths;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.readAllBytes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    private static final String MESSAGE_ID = "2B77B3F5-3016-4A6D-821F-152CE420E58D";
    private static final String TRACKING_ID = "7D6F23E0-AE1A-11DB-9808-B18E1E0994CD";
    private static final String RESPONSE_XML = "<response>";

    @InjectMocks
    private ReportController reportController;

    @Mock
    private EncounterReportService encounterReportService;

    @Mock
    private ItkResponseUtil itkResponseUtil;

    @Mock
    private ItkValidator itkValidator;

    @Mock
    private SoapValidator soapValidator;

    @Test
    public void postReportValidRequest() throws XmlException {
        when(itkResponseUtil.createSuccessResponseEntity(eq(MESSAGE_ID), anyString())).thenReturn(RESPONSE_XML);

        String validRequest = getValidXmlReportRequest();

        ResponseEntity<String> response = reportController.postReport(validRequest);

        ArgumentCaptor<POCDMT000002UK01ClinicalDocument1> captor = ArgumentCaptor.forClass(POCDMT000002UK01ClinicalDocument1.class);
        verify(encounterReportService).transformAndPopulateToGP(captor.capture(), eq(MESSAGE_ID), eq(TRACKING_ID));
        POCDMT000002UK01ClinicalDocument1 clinicalDocument = captor.getValue();
        assertThat(clinicalDocument.getId().getRoot()).isEqualTo("A709A442-3CF4-476E-8377-376500E829C9");
        assertThat(clinicalDocument.getSetId().getRoot()).isEqualTo("411910CF-1A76-4330-98FE-C345DDEE5553");
        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(RESPONSE_XML);
    }

    private String getValidXmlReportRequest() {
        try {
            URL reportXmlResource = this.getClass().getResource("/xml/ITK_Report_request.xml");
            return new String(readAllBytes(Paths.get(reportXmlResource.getPath())), defaultCharset());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void postReportInvalidXmlRequest() {
        String invalidRequest = "<invalid>";

        ResponseEntity<String> response = reportController.postReport(invalidRequest);
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }

    @Test
    public void postReportInvalidItkRequest() throws SoapClientException {
        doThrow(new SoapClientException("Soap validation failed", "ITK header missing"))
            .when(itkValidator).checkItkConformance(anyMap());
        String invalidRequest = getValidXmlReportRequest();

        ResponseEntity<String> response = reportController.postReport(invalidRequest);
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
    }
}
