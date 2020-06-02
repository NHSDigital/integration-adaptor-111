package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;
import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@RestController
public class ReportController {

    @Autowired
    private EncounterReportService encounterReportService;

    @PostMapping(value = "/report", consumes = { TEXT_XML_VALUE, APPLICATION_XML_VALUE })
    @ResponseStatus(value = ACCEPTED)
    public void postReport(@RequestBody String reportXml) throws JsonProcessingException {
        try {
            POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(reportXml);
            validate(clinicalDocument);
            encounterReportService.transformAndPopulateToGP(clinicalDocument);
        } catch (XmlException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}
