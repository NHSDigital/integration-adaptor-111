package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        Optional<String> dosServiceId = elements.stream()
            .filter(it -> it.attribute("type") != null
                && it.attributeValue("type").equals("2.16.840.1.113883.2.1.3.2.4.18.44")
            )
            .map(it -> "DOSServiceID:".concat(it.attributeValue("uri")))
            .findFirst();

        Optional<String> odsCode = elements.stream()
            .filter(it -> it.attribute("type") == null
                && it.attributeValue("uri").startsWith("urn:nhs-uk:addressing:ods:")
            )
            .map(it -> it.attributeValue("uri"))
            .findFirst();

        String address;
        if (odsCode.isPresent() && dosServiceId.isPresent()) {
            address = odsCode.get() + ":" + dosServiceId.get();
        } else {
            address = odsCode.orElseGet(dosServiceId::get);
        }

        header.setAddressList(Collections.singletonList(address));
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
