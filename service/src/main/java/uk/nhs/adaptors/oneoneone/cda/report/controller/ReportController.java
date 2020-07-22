package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.util.UUID.randomUUID;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.ADDRESS;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.DISTRIBUTION_ENVELOPE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.MESSAGE_ID;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.TRACKING_ID;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;
import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.dom4j.DocumentException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.ItkXmlException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkResponseUtil;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportParserUtil;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@RestController
@AllArgsConstructor
@Slf4j
public class ReportController {

    private static final String CLIENT_ERROR_CODE = "1000";
    private static final String INTERNAL_PROCESSING_ERROR_CODE = "2200";
    private static final String DEFAULT_ADDRESS = "http://www.w3.org/2005/08/addressing/anonymous";
    private static final String INTERNAL_USER_ERROR_MESSAGE = "Internal Error. Please contact your system Administrator";
    private static final String INTERNAL_ERROR_MESSAGE = "Internal Error";
    private static final String DEFAULT_MESSAGE_ID_MISSING = "Message Id not available";
    private final EncounterReportService encounterReportService;
    private final ItkResponseUtil itkResponseUtil;

    @PostMapping(value = "/report",
        consumes = {TEXT_XML_VALUE, APPLICATION_XML_VALUE},
        produces = TEXT_XML_VALUE)
    @ResponseStatus(value = ACCEPTED)
    public ResponseEntity<String> postReport(@RequestBody String reportXml) {
        String toAddress = null;
        String messageId = null;
        try {
            Map<ReportElement, String> reportElementsMap = ReportParserUtil.parseReportXml(reportXml);
            String trackingId = reportElementsMap.get(TRACKING_ID);
            messageId = reportElementsMap.get(MESSAGE_ID);
            toAddress = getValueOrDefaultAddress(reportElementsMap.get(ADDRESS));

            LOGGER.info("ITK SOAP message received. MessageId: {}, ItkTrackingId: {}",
                messageId, trackingId);

            POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(reportElementsMap
                .get(DISTRIBUTION_ENVELOPE));

            validate(clinicalDocument);

            encounterReportService.transformAndPopulateToGP(clinicalDocument,
                messageId, trackingId);

            return new ResponseEntity<>(itkResponseUtil.createSuccessResponseEntity(messageId, randomUUID().toString().toUpperCase()), OK);
        } catch (DocumentException e) {
            LOGGER.error(BAD_REQUEST.toString() + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, createErrorResponse(
                DEFAULT_ADDRESS, DEFAULT_MESSAGE_ID_MISSING, CLIENT_ERROR_CODE, "This is not a valid XML message", e.getMessage()));
        } catch (XmlException e) {
            LOGGER.error(BAD_REQUEST.toString() + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, createErrorResponse(
                DEFAULT_ADDRESS, DEFAULT_MESSAGE_ID_MISSING, CLIENT_ERROR_CODE, "Message body not valid", e.getMessage()));
        } catch (ItkXmlException e) {
            LOGGER.error(BAD_REQUEST.toString() + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, createErrorResponse(
                DEFAULT_ADDRESS, DEFAULT_MESSAGE_ID_MISSING, CLIENT_ERROR_CODE, e.getReason(), e.getMessage()));
        } catch (SoapClientException e) {
            LOGGER.error(BAD_REQUEST.toString() + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, createErrorResponse(
                DEFAULT_ADDRESS, DEFAULT_MESSAGE_ID_MISSING, CLIENT_ERROR_CODE, e.getReason(), e.getMessage()));
        } catch (Exception e) {
            LOGGER.error(INTERNAL_SERVER_ERROR.toString() + e.getMessage());
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, createErrorResponse(
                toAddress, messageId, INTERNAL_PROCESSING_ERROR_CODE,
                INTERNAL_USER_ERROR_MESSAGE, INTERNAL_ERROR_MESSAGE));
        }
    }

    public static String getValueOrDefaultAddress(String value) {
        return value == null ? DEFAULT_ADDRESS : value;
    }

    private String createErrorResponse(String toAddress, String messageId, String errorCode, String errorForUser, String errorMessage) {
        return itkResponseUtil.createUnSuccessfulResponseEntity(
            randomUUID().toString().toUpperCase(), toAddress, messageId,
            errorCode, randomUUID().toString().toUpperCase(), errorForUser,
            errorMessage);
    }
}