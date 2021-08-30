package uk.nhs.adaptors.oneoneone.cda.report.validation;

import java.time.OffsetDateTime;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapMustUnderstandException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.XmlUtils;
import uk.nhs.adaptors.oneoneone.config.SoapProperties;

@Component
@AllArgsConstructor
public class SoapValidator {
    private static final String REPLY_TO = "http://www.w3.org/2005/08/addressing/anonymous";
    private static final String SEND_TO_XPATH = "//*[local-name()='To']";
    private static final String TIMESTAMP_XPATH = "//*[local-name()='Timestamp']";
    private static final String TIMESTAMP_CREATED_XPATH = "//*[local-name()='Created']";
    private static final String TIMESTAMP_EXPIRES_XPATH = "//*[local-name()='Expires']";
    private static final String USERNAME_XPATH = "//*[local-name()='Username']";
    private static final String LOCAL_HEADER_XPATH = "//*[local-name()='LocalHeaderElement']";
    private static final String REPLY_TO_ADDRESS_XPATH = "//*[local-name()='ReplyTo']/*[local-name()='Address']";

    private final SoapProperties soapProperties;
    private final XmlUtils xmlUtils;

    public void checkSoapItkConformance(Element soapHeader) throws SoapClientException, SoapMustUnderstandException,
        XPathExpressionException {
        if (soapProperties.isValidationEnabled()) {
            checkSendTo(soapHeader);
            checkTimestamp(soapHeader);
            checkUsername(soapHeader);
            checkReplyTo(soapHeader);
            checkForeingHeader(soapHeader);
        }
    }

    private void checkForeingHeader(Element soapHeader) throws SoapMustUnderstandException, XPathExpressionException {
        Node localHeader = xmlUtils.getSingleNode(soapHeader, LOCAL_HEADER_XPATH);
        if (localHeader != null) {
            Attr mustUnderstand = ((Element) localHeader).getAttributeNode("mustUnderstand");
            if (mustUnderstand != null && StringUtils.equals(mustUnderstand.getValue(), "1")) {
                throw new SoapMustUnderstandException("Soap validation failed", "Rejecting foreign header");
            }
        }
    }

    private void checkReplyTo(Element soapHeader) throws SoapClientException, XPathExpressionException {
        Node replyToAddress = xmlUtils.getSingleNode(soapHeader, REPLY_TO_ADDRESS_XPATH);
        if (replyToAddress != null) {
            if (!StringUtils.equals(REPLY_TO, replyToAddress.getNodeValue())) {
                throw new SoapClientException("Soap validation failed", "Invalid ReplyTo: " + replyToAddress.getNodeValue());
            }
        }
    }

    private void checkUsername(Element soapHeader) throws SoapClientException, XPathExpressionException {
        Node username = xmlUtils.getSingleNode(soapHeader, USERNAME_XPATH);

        if (username == null) {
            throw new SoapClientException("Soap validation failed", "Username missing");
        }
    }

    private void checkTimestamp(Element soapHeader) throws SoapClientException, XPathExpressionException {
        Node timestamp = xmlUtils.getSingleNode(soapHeader, TIMESTAMP_XPATH);

        if (timestamp == null) {
            throw new SoapClientException("Soap validation failed", "Timestamp missing");
        }

        String created = xmlUtils.getSingleNode(timestamp, TIMESTAMP_CREATED_XPATH).getNodeValue();
        String expires = xmlUtils.getSingleNode(timestamp, TIMESTAMP_EXPIRES_XPATH).getNodeValue();
        if (OffsetDateTime.parse(created).isAfter(OffsetDateTime.parse(expires))) {
            throw new SoapClientException("Soap validation failed", "Invalid timestamp");
        }
    }

    private void checkSendTo(Element soapHeader) throws SoapClientException, XPathExpressionException {
        Node sendToNode = xmlUtils.getSingleNode(soapHeader, SEND_TO_XPATH);

        if (sendToNode == null) {
            throw new SoapClientException("Soap validation failed", "Send To missing");
        }

        String sendTo = sendToNode.getNodeValue();

        if (!StringUtils.equals(soapProperties.getSendTo(), sendTo)) {
            throw new SoapClientException("Soap validation failed", "Invalid Send To value: " + sendTo);
        }
    }
}
