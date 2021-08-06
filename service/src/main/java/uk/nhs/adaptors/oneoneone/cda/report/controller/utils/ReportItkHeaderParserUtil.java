package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.dom4j.Element;
import org.springframework.stereotype.Component;

@Component
public class ReportItkHeaderParserUtil {
    public static final String SPECIFICATION_NODE = "//*[local-name()='spec']";
    public static final String ITK_ADDRESS_NODE = "//*[local-name()='addresslist']/*[local-name()='address']";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String URI_ATTRIBUTE = "uri";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String TRACKING_ID_ATTRIBUTE = "trackingid";

    public ItkReportHeader getHeaderValues(Element headerElement) {
        ItkReportHeader header = new ItkReportHeader();
        header.setTrackingId(headerElement.attributeValue(TRACKING_ID_ATTRIBUTE));
        setSpecification(header, headerElement);
        setAddressList(header, headerElement);

        return header;
    }

    private void setAddressList(ItkReportHeader header, Element headerElement) {
        List<Element> elements = getElements(headerElement, ITK_ADDRESS_NODE);
        Optional<String> dosServiceId = elements.stream()
            .filter(it -> it.attribute(TYPE_ATTRIBUTE) != null
                && it.attributeValue(TYPE_ATTRIBUTE).equals("2.16.840.1.113883.2.1.3.2.4.18.44")
            )
            .map(it -> "DOSServiceID:".concat(it.attributeValue(URI_ATTRIBUTE)))
            .findFirst();

        Optional<String> odsCode = elements.stream()
            .filter(it -> it.attribute(TYPE_ATTRIBUTE) == null
                && it.attributeValue(URI_ATTRIBUTE).startsWith("urn:nhs-uk:addressing:ods:")
            )
            .map(it -> it.attributeValue(URI_ATTRIBUTE))
            .findFirst();

        String address = Stream.of(odsCode, dosServiceId)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(joining(":"));

        header.setAddressList(Collections.singletonList(address));
    }

    private void setSpecification(ItkReportHeader header, Element headerElement) {
        Element spec = getElements(headerElement, SPECIFICATION_NODE).get(0);
        header.setSpecKey(spec.attributeValue(KEY_ATTRIBUTE));
        header.setSpecVal(spec.attributeValue(VALUE_ATTRIBUTE));
    }

    private List<Element> getElements(Element element, String xpath) {
        return element.selectNodes(xpath).stream().map(it -> (Element) it).collect(toList());
    }
}
