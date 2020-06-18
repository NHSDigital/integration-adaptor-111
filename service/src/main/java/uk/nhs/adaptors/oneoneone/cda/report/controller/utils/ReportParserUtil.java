package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class ReportParserUtil {

    public enum ReportElement{
        MESSAGE_ID,
        DISTRIBUTION_ENVELOPE
    }

    public static Map<ReportElement, String> parseReportXml(String reportXml) throws DocumentException {

        Map<ReportElement, String> reportElementsMap = new HashMap<>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(reportXml));

        reportElementsMap.put(ReportElement.MESSAGE_ID, getMessageId(document));
        reportElementsMap.put(ReportElement.DISTRIBUTION_ENVELOPE, getDistributionEnvelope(document));
        return reportElementsMap;
    }

    private static String getMessageId(Document document) {
        Node messageIdNode = document.selectSingleNode("//*[local-name()='MessageID']");
        return messageIdNode.getText();
    }

    private static String getDistributionEnvelope(Document document) {
        Node distributionEnvelopeNode = document.selectSingleNode("//*[local-name()='DistributionEnvelope']");
        return distributionEnvelopeNode.asXML();
    }
}
