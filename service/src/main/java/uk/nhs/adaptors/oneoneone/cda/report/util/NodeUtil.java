package uk.nhs.gpitf.reports.util;

import java.util.Optional;
import org.apache.xmlbeans.XmlObject;
import lombok.experimental.UtilityClass;
import org.w3c.dom.Node;

@UtilityClass
public class NodeUtil {

  public String getNodeValueString(XmlObject xmlObject) {
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

  public boolean hasSubNodes(XmlObject xmlObject) {
    var node = xmlObject.getDomNode();
    return node.getChildNodes().getLength() != 1
        || node.getFirstChild().getNodeType() != Node.TEXT_NODE;
  }
}

