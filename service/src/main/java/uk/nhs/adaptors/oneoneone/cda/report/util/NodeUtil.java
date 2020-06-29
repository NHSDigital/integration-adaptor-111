package uk.nhs.adaptors.oneoneone.cda.report.util;

import lombok.experimental.UtilityClass;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

import java.util.Optional;

@UtilityClass
public class NodeUtil {

    public static String getNodeValueString(XmlObject xmlObject) {
        return Optional.ofNullable(xmlObject)
                .map(XmlObject::getDomNode)
                .map(Node::getFirstChild)
                .map(Node::getNodeValue)
                .orElse(null);
    }

    public String getAllText(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            return node.getNodeValue();
        }

        StringBuilder sb = new StringBuilder();
        Node child = node.getFirstChild();
        while (child != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(getAllText(child));
            child = child.getNextSibling();
        }
        return sb.toString();
    }

    public static boolean hasSubNodes(XmlObject xmlObject) {
        var node = xmlObject.getDomNode();
        return node.getChildNodes().getLength() != 1
                || node.getFirstChild().getNodeType() != Node.TEXT_NODE;
    }
}

