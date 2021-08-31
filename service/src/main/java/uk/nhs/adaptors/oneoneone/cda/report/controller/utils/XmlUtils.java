package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET;
import static javax.xml.xpath.XPathConstants.NODE;
import static javax.xml.xpath.XPathConstants.NODESET;
import static javax.xml.xpath.XPathConstants.STRING;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class XmlUtils {

    @SneakyThrows
    public Node getSingleNode(Node node, String xpath) {
        XPathExpression expression = XPathFactory.newInstance().newXPath().compile(xpath);
        return (Node) expression.evaluate(node, NODE);
    }

    @SneakyThrows
    public String getSingleNodeAsString(Node node, String xpath) {
        XPathExpression expression = XPathFactory.newInstance().newXPath().compile(xpath);
        return (String) expression.evaluate(node, STRING);
    }

    @SneakyThrows
    public List<Node> getNodesFromElement(Element element, String xpath) {
        XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xpath);
        NodeList nodeList = (NodeList) xPathExpression.evaluate(element, NODESET);
        var nodes = new ArrayList<Node>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(nodeList.item(i));
        }
        return nodes;
    }

    @SneakyThrows
    public String serialize(Node node) {
        var xmlOutput = new StreamResult(new StringWriter());
        transformer().transform(new DOMSource(node), xmlOutput);
        return xmlOutput.getWriter().toString();
    }

    private Transformer transformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(ACCESS_EXTERNAL_STYLESHEET, "");
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        return transformer;
    }
}
