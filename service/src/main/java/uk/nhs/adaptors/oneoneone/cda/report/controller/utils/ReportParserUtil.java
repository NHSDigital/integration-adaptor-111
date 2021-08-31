package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.DocumentBuilderUtil.parseDocument;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportParserUtil {

    private static final String MESSAGE_ID_NODE = "//*[local-name()='MessageID']";
    private static final String ADDRESS_NODE = "//*[local-name()='Address']";
    private static final String DISTRIBUTION_ENVELOPE_NODE = "/Envelope/Body/DistributionEnvelope";
    private static final String ITK_PAYLOADS_NODE = "//*[local-name()='payloads']";
    private static final String HEADER_NODE = "//*[local-name()='header']";
    private static final String SOAP_HEADER_NODE = "//*[local-name()='Header']";

    private final XmlUtils xmlUtils;

    public ReportItems parseReportXml(String reportXml) throws IOException, SAXException, ParserConfigurationException {

        ReportItems reportItems = new ReportItems();
        Document document = parseDocument(reportXml);

        reportItems.setMessageId(xmlUtils.getSingleNodeAsString(document, MESSAGE_ID_NODE));
        reportItems.setSoapAddress(xmlUtils.getSingleNodeAsString(document, ADDRESS_NODE));
        reportItems.setDistributionEnvelope(xmlUtils.getSingleNode(document, DISTRIBUTION_ENVELOPE_NODE));
        reportItems.setPayloads((Element) xmlUtils.getSingleNode(document, ITK_PAYLOADS_NODE));
        reportItems.setItkHeader((Element) xmlUtils.getSingleNode(document, HEADER_NODE));
        reportItems.setSoapHeader((Element) xmlUtils.getSingleNode(document, SOAP_HEADER_NODE));

        return reportItems;
    }
}
