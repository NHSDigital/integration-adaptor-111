package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ReportItkHeaderParserUtil {
    private static final String SPECIFICATION_NODE = "//*[local-name()='spec']";
    private static final String ITK_ADDRESS_NODE = "//*[local-name()='addresslist']/*[local-name()='address']";

    private final XmlUtils xmlUtils;

    public ItkReportHeader getHeaderValues(Element headerElement) throws XPathExpressionException {
        ItkReportHeader header = new ItkReportHeader();
        header.setTrackingId(headerElement.getAttribute("trackingid"));
        setSpecification(header, headerElement);
        setAddressList(header, headerElement);

        return header;
    }

    private void setAddressList(ItkReportHeader header, Element headerElement) throws XPathExpressionException {
        List<Element> elements = getElements(headerElement, ITK_ADDRESS_NODE);
        List<String> addresses = elements.stream().map(it -> it.getAttribute("uri")).collect(toList());
        header.setAddressList(addresses);
    }

    private void setSpecification(ItkReportHeader header, Element headerElement) throws XPathExpressionException {
        Element spec = getElements(headerElement, SPECIFICATION_NODE).get(0);
        header.setSpecKey(spec.getAttribute("key"));
        header.setSpecVal(spec.getAttribute("value"));
    }

    private List<Element> getElements(Element element, String xpath) throws XPathExpressionException {
        return xmlUtils.getNodesFromElement(element, xpath).stream().map(it -> (Element) it).collect(toList());
    }
}
