package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;

public class ReportParserUtil {

    private static final String MESSAGE_ID_NODE = "//*[local-name()='MessageID']";
    private static final String ADDRESS_NODE = "//*[local-name()='Address']";
    private static final String DISTRIBUTION_ENVELOPE_NODE = "//*[local-name()='DistributionEnvelope']";
    private static final String HEADER_NODE = "//*[local-name()='header']";

    public static Map<ReportElement, String> parseReportXml(String reportXml) throws DocumentException, SoapClientException {

        Map<ReportElement, String> reportElementsMap = new HashMap<>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(reportXml));

        reportElementsMap.put(ReportElement.MESSAGE_ID, getMessageId(document));
        reportElementsMap.put(ReportElement.ADDRESS, getAddress(document));
        reportElementsMap.put(ReportElement.DISTRIBUTION_ENVELOPE, getDistributionEnvelope(document));
        reportElementsMap.put(ReportElement.TRACKING_ID, getTrackingId(document));
        return reportElementsMap;
    }

    private static String getMessageId(Document document) throws SoapClientException {
        String messageId = "MessageId";
        String errorLocation = "Header";
        Node messageIdNode = document.selectSingleNode(MESSAGE_ID_NODE);
        if (messageIdNode.getText() == null) {
            throw new SoapClientException(messageId, errorLocation);
        }
        return messageIdNode.getText();
    }

    private static String getAddress(Document document) {
        Node messageIdNode = document.selectSingleNode(ADDRESS_NODE);
        if (messageIdNode == null) {
            return null;
        }
        return messageIdNode.getText();
    }

    private static String getDistributionEnvelope(Document document) throws SoapClientException {
        String distributionEnvelope = "DistributionEnvelope";
        String errorLocation = "Payload";
        Node distributionEnvelopeNode = document.selectSingleNode(DISTRIBUTION_ENVELOPE_NODE);
        if (distributionEnvelopeNode.getText() == null) {
            throw new SoapClientException(distributionEnvelope, errorLocation);
        }
        return distributionEnvelopeNode.asXML();
    }

    private static String getTrackingId(Document document) throws SoapClientException {
        String trackingId = "trackingid";
        String errorLocation = "DistributionEnvelope header";
        Element element = (Element) document.selectSingleNode(HEADER_NODE);
        if (element.attribute(trackingId).getValue() == null) {
            throw new SoapClientException(trackingId, errorLocation);
        }
        return element.attribute(trackingId).getValue();
    }
}
