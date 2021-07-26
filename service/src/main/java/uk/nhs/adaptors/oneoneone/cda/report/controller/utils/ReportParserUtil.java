package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.DocumentBuilderUtil.parseDocument;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.DISTRIBUTION_ENVELOPE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.ITK_HEADER;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.ITK_PAYLOADS;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.MESSAGE_ID;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.SOAP_ADDRESS;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.SOAP_HEADER;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

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
    private static final String DISTRIBUTION_ENVELOPE_NODE = "//*[local-name()='DistributionEnvelope']";
    private static final String ITK_PAYLOADS_NODE = "//*[local-name()='payloads']";
    private static final String HEADER_NODE = "//*[local-name()='header']";
    private static final String SOAP_HEADER_NODE = "//*[local-name()='Header']";

    private final XmlUtils xmlUtils;

    public Map<ReportElement, Element> parseReportXml(String reportXml) throws XPathExpressionException {

        Map<ReportElement, Element> reportElementsMap = new HashMap<>();
        Document document = null;
        try { // todo ogarnac exceptiony
            document = parseDocument(reportXml);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        reportElementsMap.put(MESSAGE_ID, xmlUtils.getElement(document, MESSAGE_ID_NODE));
        reportElementsMap.put(SOAP_ADDRESS, xmlUtils.getElement(document, ADDRESS_NODE));
        reportElementsMap.put(DISTRIBUTION_ENVELOPE, xmlUtils.getElement(document, DISTRIBUTION_ENVELOPE_NODE));
        reportElementsMap.put(ITK_PAYLOADS, xmlUtils.getElement(document, ITK_PAYLOADS_NODE));
        reportElementsMap.put(ITK_HEADER, xmlUtils.getElement(document, HEADER_NODE));
        reportElementsMap.put(SOAP_HEADER, xmlUtils.getElement(document, SOAP_HEADER_NODE));

        return reportElementsMap;
    }
}
