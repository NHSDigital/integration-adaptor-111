package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.Optional;

import lombok.experimental.UtilityClass;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Node;

@UtilityClass
public class NodeUtil {

  public static String getNodeValueString(XmlObject xmlObject) {
    return Optional.ofNullable(xmlObject)
        .map(XmlObject::getDomNode)
        .map(Node::getFirstChild)
        .map(Node::getNodeValue)
        .orElse(null);
  }

  public static boolean hasSubNodes(XmlObject xmlObject) {
    var node = xmlObject.getDomNode();
    return node.getChildNodes().getLength() != 1
        || node.getFirstChild().getNodeType() != Node.TEXT_NODE;
  }
}

