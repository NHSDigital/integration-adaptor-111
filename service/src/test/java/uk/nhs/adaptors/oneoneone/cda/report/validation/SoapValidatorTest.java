package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import javax.xml.xpath.XPathExpressionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.XmlUtils;
import uk.nhs.adaptors.oneoneone.config.SoapProperties;

@ExtendWith(MockitoExtension.class)
public class SoapValidatorTest {
    private static final String SOAP_VALIDATION_FAILED_MSG = "Soap validation failed";
    private static final String SEND_TO_XPATH = "//*[local-name()='To']";
    private static final String TIMESTAMP_XPATH = "//*[local-name()='Timestamp']";
    private static final String TIMESTAMP_CREATED_XPATH = "//*[local-name()='Created']";
    private static final String TIMESTAMP_EXPIRES_XPATH = "//*[local-name()='Expires']";
    private static final String USERNAME_XPATH = "//*[local-name()='Username']";
    private static final String REPLY_TO_ADDRESS_XPATH = "//*[local-name()='ReplyTo']/*[local-name()='Address']";
    private static final String VALID_SOAP_TO = "http//localhost:8080/report";
    private static final String VALID_REPLY_TO = "http://www.w3.org/2005/08/addressing/anonymous";

    @Mock
    private SoapProperties soapProperties;

    @InjectMocks
    private SoapValidator soapValidator;

    @Mock
    private Element soapHeader;
    @Mock
    private Node to;
    @Mock
    private Node timestamp;
    @Mock
    private Node timestampCreated;
    @Mock
    private Node timestampExpires;
    @Mock
    private Node username;
    @Mock
    private Node replyToAddress;
    @Mock
    private XmlUtils xmlUtils;

    @BeforeEach
    public void setUp() throws XPathExpressionException  {
        when(soapProperties.isValidationEnabled()).thenReturn(true);
        lenient().when(xmlUtils.getSingleNode(soapHeader, SEND_TO_XPATH)).thenReturn(to);
        lenient().when(soapProperties.getSendTo()).thenReturn(VALID_SOAP_TO);
        lenient().when(to.getNodeValue()).thenReturn(VALID_SOAP_TO);
        lenient().when(xmlUtils.getSingleNode(soapHeader, TIMESTAMP_XPATH)).thenReturn(timestamp);
        lenient().when(timestampCreated.getNodeValue()).thenReturn("2020-09-03T11:27:30Z");
        lenient().when(xmlUtils.getSingleNode(timestamp, TIMESTAMP_CREATED_XPATH)).thenReturn(timestampCreated);
        lenient().when(timestampExpires.getNodeValue()).thenReturn("2020-09-04T11:27:30Z");
        lenient().when(xmlUtils.getSingleNode(timestamp, TIMESTAMP_EXPIRES_XPATH)).thenReturn(timestampExpires);
        lenient().when(xmlUtils.getSingleNode(soapHeader, USERNAME_XPATH)).thenReturn(username);
        lenient().when(replyToAddress.getNodeValue()).thenReturn(VALID_REPLY_TO);
        lenient().when(xmlUtils.getSingleNode(soapHeader, REPLY_TO_ADDRESS_XPATH)).thenReturn(replyToAddress);
    }

    @Test
    public void shouldFailWhenSoapToIsInvalid() {
        String invalidTo = "InvalidTo";
        when(to.getNodeValue()).thenReturn(invalidTo);

        checkExceptionThrownAndErrorMessage("Invalid Send To value: " + invalidTo);
    }

    @Test
    public void shouldFailWhenSoapToIsMissing() throws XPathExpressionException {
        when(xmlUtils.getSingleNode(soapHeader, SEND_TO_XPATH)).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Send To missing");
    }

    @Test
    public void shouldFailWhenTimestampIsMissing() throws XPathExpressionException {
        when(xmlUtils.getSingleNode(soapHeader, SEND_TO_XPATH)).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Send To missing");
    }

    @Test
    public void shouldFailWhenTimestampIsInvalid() {
        when(timestampExpires.getNodeValue()).thenReturn("2018-09-04T11:27:30Z");

        checkExceptionThrownAndErrorMessage("Invalid timestamp");
    }

    @Test
    public void shouldFailWhenUsernameIsMissing() throws XPathExpressionException {
        when(xmlUtils.getSingleNode(soapHeader, USERNAME_XPATH)).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Username missing");
    }

    @Test
    public void shouldFailWhenReplyToInvalid() {
        String invalidReplyTo = "InvalidReplyTo";
        when(replyToAddress.getNodeValue()).thenReturn(invalidReplyTo);

        checkExceptionThrownAndErrorMessage("Invalid ReplyTo: InvalidReplyTo");
    }

    @Test
    public void shouldNotFailWhenValidationDisabled() {
        reset(soapHeader);
        when(soapProperties.isValidationEnabled()).thenReturn(false);

        assertDoesNotThrow(() -> soapValidator.checkSoapItkConformance(soapHeader));
    }

    private void checkExceptionThrownAndErrorMessage(String errorMessage) {
        boolean exceptionThrown = false;
        try {
            soapValidator.checkSoapItkConformance(soapHeader);
        } catch (SoapClientException e) {
            exceptionThrown = true;
            assertThat(e.getReason()).isEqualTo(errorMessage);
            assertThat(e.getMessage()).isEqualTo(SOAP_VALIDATION_FAILED_MSG);
        } catch (Exception e) {
            fail("Unexpected exception thrown");
        }

        assertThat(exceptionThrown).isTrue();
    }
}
