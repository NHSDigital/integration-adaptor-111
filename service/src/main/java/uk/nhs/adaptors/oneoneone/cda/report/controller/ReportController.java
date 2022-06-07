package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.util.UUID.randomUUID;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

import org.apache.xmlbeans.XmlException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.ItkXmlException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapMustUnderstandException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkReportHeader;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkResponseUtil;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItems;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportParserUtil;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.adaptors.oneoneone.cda.report.validation.ItkAddressValidator;
import uk.nhs.adaptors.oneoneone.cda.report.validation.ItkValidator;
import uk.nhs.adaptors.oneoneone.cda.report.validation.SoapValidator;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;
import org.springframework.web.bind.annotation.CrossOrigin;

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
    private final ReportItkHeaderParserUtil headerParserUtil;
    private final ItkAddressValidator itkAddressValidator;
    private final ReportParserUtil reportParserUtil;
    private final ReportRequestUtils reportRequestUtils;

    @CrossOrigin("http://localhost:3000")
    @PostMapping(value = "/report",
        consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE},
        produces = TEXT_XML_VALUE
    )
    @ResponseStatus(value = ACCEPTED)
    public ResponseEntity<String> postReport(@RequestBody String reportXml) {
        String toAddress = null;
        String messageId;
        try {
            ReportItems reportItems = reportParserUtil.parseReportXml(reportXml);
            itkValidator.checkItkConformance(reportItems);
            soapValidator.checkSoapItkConformance(reportItems.getSoapHeader());
            itkAddressValidator.checkItkOdsAndDosId(reportItems.getItkHeader());
            ItkReportHeader headerValues = headerParserUtil.getHeaderValues(reportItems.getItkHeader());
            toAddress = getValueOrDefaultAddress(reportItems.getSoapAddress());

            LOGGER.info("ITK SOAP message received. MessageId: {}, ItkTrackingId: {}",
                reportItems.getMessageId(), headerValues.getTrackingId());

            DistributionEnvelopeDocument distributionEnvelope = reportRequestUtils.extractDistributionEnvelope(reportItems
                .getDistributionEnvelope());
            validate(distributionEnvelope);
            POCDMT000002UK01ClinicalDocument1 clinicalDocument = reportRequestUtils.extractClinicalDocument(distributionEnvelope);

            validate(clinicalDocument);

            encounterReportService.transformAndPopulateToGP(clinicalDocument, reportItems.getMessageId(), headerValues);

            return new ResponseEntity<>(
                itkResponseUtil.createSuccessResponseEntity(reportItems.getMessageId(), randomUUID().toString().toUpperCase()), OK);
        } catch (SAXException e) {
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

    public static String getValueOrDefaultAddress(String value) {
        return value == null ? DEFAULT_ADDRESS : value;
    }

    private String createErrorResponseBody(String toAddress, String errorCode, String faultCode, String errorForUser, String errorMessage) {
        return itkResponseUtil.createUnSuccessfulResponseEntity(
            randomUUID().toString().toUpperCase(), toAddress,
            errorCode, faultCode, randomUUID().toString().toUpperCase(), errorForUser,
            errorMessage);
    }
}