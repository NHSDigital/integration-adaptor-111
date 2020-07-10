package uk.nhs.adaptors.oneoneone.cda.report.controller;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.MESSAGE_ID;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.TRACKING_ID;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;
import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

import java.util.Map;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;
import static uk.nhs.adaptors.oneoneone.xml.XmlValidator.validate;

import java.util.Collections;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.xmlbeans.XmlException;
import org.dom4j.DocumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportParserUtil;
import uk.nhs.adaptors.oneoneone.cda.report.service.EncounterReportService;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@RestController
@AllArgsConstructor
@Slf4j
public class ReportController {
    public static final String ACTION_RESPONSE = "urn:nhs-itk:services:201005:SendNHS111Report-v2-0Response";
    public static final String HTTP_HEADER_MESSAGE_ID = "MessageId";

    private final EncounterReportService encounterReportService;

    @PostMapping(value = "/report",
        consumes = {TEXT_XML_VALUE, APPLICATION_XML_VALUE},
        produces = {TEXT_XML_VALUE, APPLICATION_XML_VALUE})
    @ResponseStatus(value = ACCEPTED)
    public ResponseEntity<?> postReport(@RequestBody String reportXml) {
        try {
            Map<ReportElement, String> reportElementsMap = ReportParserUtil.parseReportXml(reportXml);
            LOGGER.info("ITK SOAP message received. MessageId: {}, ItkTrackingId: {}",
                reportElementsMap.get(MESSAGE_ID), reportElementsMap.get(TRACKING_ID));
            POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(reportElementsMap
                .get(ReportElement.DISTRIBUTION_ENVELOPE));
            validate(clinicalDocument);

            encounterReportService.transformAndPopulateToGP(clinicalDocument,
                reportElementsMap.get(ReportElement.MESSAGE_ID));

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            String messageID = reportElementsMap.get(ReportElement.MESSAGE_ID);
            headers.put(HTTP_HEADER_MESSAGE_ID, Collections.singletonList(messageID));

            SOAPMessage message = createSimpleMessageResponseSOAPPart(messageID);

            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        } catch (XmlException | DocumentException | SOAPException e) {
            LOGGER.error(BAD_REQUEST.toString() + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    private SOAPMessage createSimpleMessageResponseSOAPPart(String messageID) throws SOAPException {
        SOAPMessage message = MessageFactory.newInstance().createMessage();
        SOAPPart sPart = message.getSOAPPart();
        SOAPEnvelope env = sPart.getEnvelope();

        SOAPHeader header = env.getHeader();
        header.addHeaderElement(env.createName("MessageID", "wsa", ""))
            .addTextNode(String.valueOf(newRandomUuid()));
        header.addHeaderElement(env.createName("Action", "wsa", ""))
            .addTextNode(ACTION_RESPONSE);

        SOAPBody body = env.getBody();
        Name ns = env.createName("SimpleMessageResponse", "itk:",
            "urn:nhs-itk:ns:201005");
        SOAPBodyElement bodyElement = body.addBodyElement(ns);
        SOAPElement ele = bodyElement.addChildElement(ns);
        ele.addTextNode("MESSAGE_ID:" + messageID);

        return message;
    }
}