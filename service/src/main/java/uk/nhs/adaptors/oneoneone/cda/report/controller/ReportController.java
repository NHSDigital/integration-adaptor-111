package uk.nhs.adaptors.oneoneone.cda.report.controller;

import lombok.AllArgsConstructor;
import org.apache.xmlbeans.XmlException;
import org.dom4j.DocumentException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportParserUtil;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;
import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

@RestController
@AllArgsConstructor
public class ReportController {

    private EncounterReportService encounterReportService;

    @PostMapping(value = "/report", consumes = {TEXT_XML_VALUE, APPLICATION_XML_VALUE})
    @ResponseStatus(value = ACCEPTED)
    public void postReport(@RequestBody String reportXml) {
        try {
            Map<ReportElement, String> reportElementsMap = ReportParserUtil.parseReportXml(reportXml);

            POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(reportElementsMap
                    .get(ReportElement.DISTRIBUTION_ENVELOPE));
            validate(clinicalDocument);

            encounterReportService.transformAndPopulateToGP(clinicalDocument, reportElementsMap.get(ReportElement.MESSAGE_ID));
        } catch (XmlException | DocumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}
