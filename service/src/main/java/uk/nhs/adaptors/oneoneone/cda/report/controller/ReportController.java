package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static java.util.UUID.randomUUID;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

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
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ItkResponseUtil;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportParserUtil;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@RestController
@AllArgsConstructor
@Slf4j
public class ReportController {

    private final EncounterReportService encounterReportService;
    private final ItkResponseUtil itkResponseUtil;

    @PostMapping(value = "/report",
        consumes = {TEXT_XML_VALUE, APPLICATION_XML_VALUE},
        produces = TEXT_XML_VALUE)
    @ResponseStatus(value = ACCEPTED)
    public ResponseEntity<String> postReport(@RequestBody String reportXml) {
        try {
            Map<ReportElement, String> reportElementsMap = ReportParserUtil.parseReportXml(reportXml);
            String messageId = reportElementsMap.get(MESSAGE_ID);
            String trackingId = reportElementsMap.get(TRACKING_ID);
            LOGGER.info("ITK SOAP message received. MessageId: {}, ItkTrackingId: {}",
                messageId, trackingId);
            POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(reportElementsMap
                .get(DISTRIBUTION_ENVELOPE));
            validate(clinicalDocument);

            encounterReportService.transformAndPopulateToGP(clinicalDocument,
                messageId, trackingId);

            return new ResponseEntity<>(itkResponseUtil.createSuccessResponseEntity(messageId, randomUUID().toString().toUpperCase()), OK);
        } catch (XmlException | DocumentException e) {
            LOGGER.error(BAD_REQUEST.toString() + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}