package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

@Component
public class ReportItkHeaderParserUtil {
    private static final String SPECIFICATION_NODE = "//*[local-name()='spec']";
    private static final String ITK_ADDRESS_NODE = "//*[local-name()='addresslist']/*[local-name()='address']";

    public ItkReportHeader getHeaderValues(Element headerElement) {
        ItkReportHeader header = new ItkReportHeader();
        header.setTrackingId(headerElement.attributeValue("trackingid"));
        setSpecification(header, headerElement);
        setAddressList(header, headerElement);

        return header;
    }

    private void setAddressList(ItkReportHeader header, Element headerElement) {
        List<Element> elements = getElements(headerElement, ITK_ADDRESS_NODE);
        List<String> addresses = elements.stream().map(it -> it.attributeValue("uri")).collect(toList());
        header.setAddressList(addresses);
    }

    private void setSpecification(ItkReportHeader header, Element headerElement) {
        Element spec = getElements(headerElement, SPECIFICATION_NODE).get(0);
        header.setSpecKey(spec.attributeValue("key"));
        header.setSpecVal(spec.attributeValue("value"));
    }

    private List<Element> getElements(Element element, String xpath) {
        return element.selectNodes(xpath).stream().map(it -> (Element) it).collect(toList());
    }
}
