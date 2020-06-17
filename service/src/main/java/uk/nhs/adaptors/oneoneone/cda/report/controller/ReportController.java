package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;
import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

import java.io.StringReader;
import java.net.URL;

import org.apache.xmlbeans.XmlException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.tools.javac.Main;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeType;

@RestController
@AllArgsConstructor
public class ReportController {

    private EncounterReportService encounterReportService;

    @PostMapping(value = "/report", consumes = { TEXT_XML_VALUE, APPLICATION_XML_VALUE })
    @ResponseStatus(value = ACCEPTED)
    public void postReport(@RequestBody String reportXml) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(new StringReader(reportXml));

            String distributionEnvelope = getDistributionEnvelope(document);
            //TODO: this message id will be used in response
            String messageId = getMessageId(document);

            POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(distributionEnvelope);
            validate(clinicalDocument);

            encounterReportService.transformAndPopulateToGP(clinicalDocument);
        } catch (XmlException | DocumentException e) {
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    private String getMessageId(Document document) {
        Node headerNode = document.selectSingleNode("//*[local-name()='Header']");
        Node messageIdNode = headerNode.selectSingleNode("//*[local-name()='MessageID']");
        return messageIdNode.getText();
    }

    private String getDistributionEnvelope(Document document) {
        Node distributionEnvelopeNode = document.selectSingleNode("//*[local-name()='DistributionEnvelope']");
        return distributionEnvelopeNode.asXML();
    }
}
