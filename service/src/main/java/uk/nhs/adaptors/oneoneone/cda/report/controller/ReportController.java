package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.util.UUID.randomUUID;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.ADDRESS;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.DISTRIBUTION_ENVELOPE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.ITK_HEADER;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.MESSAGE_ID;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.SOAP_HEADER;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractDistributionEnvelope;
import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.ItkXmlException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapMustUnderstandException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkResponseUtil;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportParserUtil;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.adaptors.oneoneone.cda.report.validation.ItkValidator;
import uk.nhs.adaptors.oneoneone.cda.report.validation.SoapValidator;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;

@RestController
@AllArgsConstructor
@Slf4j
public class ReportController {

    private static final String CLIENT_ERROR_CODE = "1000";
    private static final String INTERNAL_PROCESSING_ERROR_CODE = "2200";
    private static final String FAULT_CODE_CLIENT = "Client";
    private static final String FAULT_CODE_MUSTUNDERSTAND = "MustUnderstand";
    private static final String DEFAULT_ADDRESS = "http://www.w3.org/2005/08/addressing/anonymous";
    private static final String INTERNAL_USER_ERROR_MESSAGE = "Internal Error. Please contact your system Administrator";
    private static final String INTERNAL_ERROR_MESSAGE = "Internal Error";
    private final EncounterReportService encounterReportService;
    private final ItkResponseUtil itkResponseUtil;
    private final ItkValidator itkValidator;
    private final SoapValidator soapValidator;

    @PostMapping(value = "/report",
        consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE},
        produces = TEXT_XML_VALUE
    )
    @ResponseStatus(value = ACCEPTED)
    public ResponseEntity<String> postReport(@RequestBody String reportXml) {
        String toAddress = null;
        String messageId = null;
        try {
            Map<ReportElement, Element> reportElementsMap = ReportParserUtil.parseReportXml(reportXml);
            itkValidator.checkItkConformance(reportElementsMap);
            soapValidator.checkSoapItkConformance(reportElementsMap.get(SOAP_HEADER));
            String trackingId = reportElementsMap.get(ITK_HEADER).attribute("trackingid").getValue();
            messageId = reportElementsMap.get(MESSAGE_ID).getText();
            toAddress = getValueOrDefaultAddress(reportElementsMap.get(ADDRESS));

            LOGGER.info("ITK SOAP message received. MessageId: {}, ItkTrackingId: {}",
                messageId, trackingId);

            DistributionEnvelopeDocument distributionEnvelope = extractDistributionEnvelope(reportElementsMap
                .get(DISTRIBUTION_ENVELOPE));
            validate(distributionEnvelope);
            POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(distributionEnvelope);

            validate(clinicalDocument);

            encounterReportService.transformAndPopulateToGP(clinicalDocument,
                messageId, trackingId);

            return new ResponseEntity<>(itkResponseUtil.createSuccessResponseEntity(messageId, randomUUID().toString().toUpperCase()), OK);
        } catch (DocumentException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(createErrorResponseBody(
                DEFAULT_ADDRESS, CLIENT_ERROR_CODE, FAULT_CODE_CLIENT, "This is not a valid XML message", e.getMessage()),
                INTERNAL_SERVER_ERROR);
        } catch (XmlException e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(createErrorResponseBody(
                DEFAULT_ADDRESS, CLIENT_ERROR_CODE, FAULT_CODE_CLIENT, "schema validation failed", e.getMessage()),
                INTERNAL_SERVER_ERROR);
        } catch (ItkXmlException e) {
            LOGGER.error(e.getReason(), e);
            return new ResponseEntity<>(createErrorResponseBody(
                DEFAULT_ADDRESS, CLIENT_ERROR_CODE, FAULT_CODE_CLIENT, e.getReason(), e.getMessage()), INTERNAL_SERVER_ERROR);
        } catch (SoapClientException e) {
            LOGGER.error(e.getReason(), e);
            return new ResponseEntity<>(createErrorResponseBody(
                DEFAULT_ADDRESS, CLIENT_ERROR_CODE, FAULT_CODE_CLIENT, e.getReason(), e.getMessage()), INTERNAL_SERVER_ERROR);
        } catch (SoapMustUnderstandException e) {
            LOGGER.error(e.getReason(), e);
            return new ResponseEntity<>(createErrorResponseBody(
                DEFAULT_ADDRESS, CLIENT_ERROR_CODE, FAULT_CODE_MUSTUNDERSTAND, e.getReason(), e.getMessage()), INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new ResponseEntity<>(createErrorResponseBody(
                toAddress, INTERNAL_PROCESSING_ERROR_CODE, FAULT_CODE_CLIENT, INTERNAL_USER_ERROR_MESSAGE, INTERNAL_ERROR_MESSAGE),
                INTERNAL_SERVER_ERROR);
        }
    }

    public static String getValueOrDefaultAddress(Element value) {
        return value == null ? DEFAULT_ADDRESS : value.getText();
    }

    private String createErrorResponseBody(String toAddress, String errorCode, String faultCode, String errorForUser, String errorMessage) {
        return itkResponseUtil.createUnSuccessfulResponseEntity(
            randomUUID().toString().toUpperCase(), toAddress,
            errorCode, faultCode, randomUUID().toString().toUpperCase(), errorForUser,
            errorMessage);
    }
}