package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class ReportParserUtil {

    private static final String MESSAGE_ID_NODE = "//*[local-name()='MessageID']";
    private static final String DISTRIBUTION_ENVELOPE_NODE = "//*[local-name()='DistributionEnvelope']";
    private static final String HEADER_NODE = "//*[local-name()='header']";

    public static Map<ReportElement, String> parseReportXml(String reportXml) throws DocumentException {

        Map<ReportElement, String> reportElementsMap = new HashMap<>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(reportXml));

        reportElementsMap.put(ReportElement.MESSAGE_ID, getMessageId(document));
        reportElementsMap.put(ReportElement.DISTRIBUTION_ENVELOPE, getDistributionEnvelope(document));
        reportElementsMap.put(ReportElement.TRACKING_ID, getTrackingId(document));
        return reportElementsMap;
    }

    private static String getMessageId(Document document) {
        Node messageIdNode = document.selectSingleNode(MESSAGE_ID_NODE);
        return messageIdNode.getText();
    }

    private static String getDistributionEnvelope(Document document) {
        Node distributionEnvelopeNode = document.selectSingleNode(DISTRIBUTION_ENVELOPE_NODE);
        return distributionEnvelopeNode.asXML();
    }

    private static String getTrackingId(Document document) {
        String trackingId = "trackingid";
        Element element = (Element) document.selectSingleNode(HEADER_NODE);
        return element.attribute(trackingId).getValue();
    }
}
