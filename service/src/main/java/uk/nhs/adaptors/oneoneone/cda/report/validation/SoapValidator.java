package uk.nhs.adaptors.oneoneone.cda.report.validation;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapMustUnderstandException;
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

    public void checkSoapItkConformance(Element soapHeader) throws SoapClientException, SoapMustUnderstandException {
        if (soapProperties.isValidationEnabled()) {
            checkSendTo(soapHeader);
            checkTimestamp(soapHeader);
            checkUsername(soapHeader);
            checkReplyTo(soapHeader);
            checkForeingHeader(soapHeader);
        }
    }

    private void checkForeingHeader(Element soapHeader) throws SoapMustUnderstandException {
        Node localHeader = soapHeader.selectSingleNode(LOCAL_HEADER_XPATH);
        if (localHeader != null) {
            Attribute mustUnderstand = ((Element) localHeader).attribute("mustUnderstand");
            if (mustUnderstand != null && StringUtils.equals(mustUnderstand.getValue(), "1")) {
                throw new SoapMustUnderstandException("Soap validation failed", "Rejecting foreign header");
            }
        }
    }

    private void checkReplyTo(Element soapHeader) throws SoapClientException, SoapMustUnderstandException {
        Node replyToAddress = soapHeader.selectSingleNode(REPLY_TO_ADDRESS_XPATH);
        if (replyToAddress != null) {
            if (!StringUtils.equals(REPLY_TO, replyToAddress.getText())) {
                throw new SoapClientException("Soap validation failed", "Invalid ReplyTo: " + replyToAddress.getText());
            }
        }
    }

    private void checkUsername(Element soapHeader) throws SoapClientException {
        Node username = soapHeader.selectSingleNode(USERNAME_XPATH);

        if (username == null) {
            throw new SoapClientException("Soap validation failed", "Username missing");
        }
    }

    private void checkTimestamp(Element soapHeader) throws SoapClientException {
        Node timestamp = soapHeader.selectSingleNode(TIMESTAMP_XPATH);

        if (timestamp == null) {
            throw new SoapClientException("Soap validation failed", "Timestamp missing");
        }

        String created = timestamp.selectSingleNode(TIMESTAMP_CREATED_XPATH).getText();
        String expires = timestamp.selectSingleNode(TIMESTAMP_EXPIRES_XPATH).getText();
        if (OffsetDateTime.parse(created).isAfter(OffsetDateTime.parse(expires))) {
            throw new SoapClientException("Soap validation failed", "Invalid timestamp");
        }
    }

    private void checkSendTo(Element soapHeader) throws SoapClientException {
        Node sendToNode = soapHeader.selectSingleNode(SEND_TO_XPATH);

        if (sendToNode == null) {
            throw new SoapClientException("Soap validation failed", "Send To missing");
        }

        String sendTo = sendToNode.getText();

        if (!StringUtils.equals(soapProperties.getSendTo(), sendTo)) {
            throw new SoapClientException("Soap validation failed", "Invalid Send To value: " + sendTo);
        }
    }
}
