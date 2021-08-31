package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportItems {
    private String messageId;
    private String soapAddress;
    private Node distributionEnvelope;
    private Element payloads;
    private Element itkHeader;
    private Element soapHeader;
}
