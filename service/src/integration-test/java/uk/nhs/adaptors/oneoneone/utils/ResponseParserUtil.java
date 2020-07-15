package uk.nhs.adaptors.oneoneone.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

@Component
public class ResponseParserUtil {
    private static final String MESSAGE_ID_NODE = "//*[local-name()='MessageID']";
    private static final String ACTION_NODE = "//*[local-name()='Action']";
    private static final String SIMPLE_RESPONSE_NODE = "//*[local-name()='SimpleMessageResponse']";

    public Map<ResponseElement, String> parseSuccessfulResponseXml(String response) throws DocumentException {

        Map<ResponseElement, String> responseElementsMap = new HashMap<>();

        SAXReader reader = new SAXReader();
        Document document = reader.read(new StringReader(response));

        responseElementsMap.put(ResponseElement.MESSAGE_ID, getMessageId(document));
        responseElementsMap.put(ResponseElement.ACTION, getAction(document));
        responseElementsMap.put(ResponseElement.BODY, getBody(document));
        return responseElementsMap;
    }

    private static String getMessageId(Document document) {
        Node messageIdNode = document.selectSingleNode(MESSAGE_ID_NODE);
        return messageIdNode.getText();
    }

    private static String getAction(Document document) {
        Node actionNode = document.selectSingleNode(ACTION_NODE);
        return actionNode.getText();
    }

    private static String getBody(Document document) {
        Node simpleResponseNode = document.selectSingleNode(SIMPLE_RESPONSE_NODE);
        return simpleResponseNode.asXML();
    }
}
