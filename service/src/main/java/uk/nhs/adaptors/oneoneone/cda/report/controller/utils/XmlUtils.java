package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class XmlUtils {

    private final XPathFactory xPathFactory;

    public Element getElement(Document document, String xpath) throws XPathExpressionException {
        XPathExpression expression = xPathFactory.newXPath().compile(xpath);
        return (Element) expression.evaluate(document, XPathConstants.NODE); // albo node?
    }

    public Node getSingleNode(Element element, String xpath) throws XPathExpressionException {
        XPathExpression expression = xPathFactory.newXPath().compile(xpath);
        return (Node) expression.evaluate(element, XPathConstants.NODE);
    }

    public Node getSingleNode(Node node, String xpath) throws XPathExpressionException {
        XPathExpression expression = xPathFactory.newXPath().compile(xpath);
        return (Node) expression.evaluate(node, XPathConstants.NODE);
    }

    public List<Node> getNodesFromElement(Element element, String xpath) throws XPathExpressionException {
        XPathExpression xPathExpression = xPathFactory.newXPath().compile(xpath);
        NodeList nodeList = (NodeList) xPathExpression.evaluate(element, XPathConstants.NODESET);
        var nodes = new ArrayList<Node>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }
        return nodes;
    }
}
