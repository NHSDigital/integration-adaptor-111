package uk.nhs.adaptors.oneoneone.utils;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.DocumentBuilderUtil.parseDocument;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.XmlUtils;

@Component
@RequiredArgsConstructor
public class ResponseParserUtil {
    private static final String MESSAGE_ID_NODE = "//*[local-name()='MessageID']";
    private static final String ACTION_NODE = "//*[local-name()='Action']";
    private static final String SIMPLE_RESPONSE_NODE = "//*[local-name()='SimpleMessageResponse']";

    private final XmlUtils xmlUtils;

    public Map<ResponseElement, String> parseSuccessfulResponseXml(String response) throws IOException, SAXException,
        ParserConfigurationException {

        Map<ResponseElement, String> responseElementsMap = new HashMap<>();

        Document document = parseDocument(response);

        responseElementsMap.put(ResponseElement.MESSAGE_ID, getMessageId(document));
        responseElementsMap.put(ResponseElement.ACTION, getAction(document));
        responseElementsMap.put(ResponseElement.BODY, getBody(document));
        return responseElementsMap;
    }

    private String getMessageId(Document document) {
        Node messageIdNode = xmlUtils.getSingleNode(document, MESSAGE_ID_NODE);
        return messageIdNode.getTextContent();
    }

    private String getAction(Document document) {
        Node actionNode = xmlUtils.getSingleNode(document, ACTION_NODE);
        return actionNode.getTextContent();
    }

    private String getBody(Document document) {
        Node simpleResponseNode = xmlUtils.getSingleNode(document, SIMPLE_RESPONSE_NODE);
        return xmlUtils.serialize(simpleResponseNode);
    }
}
