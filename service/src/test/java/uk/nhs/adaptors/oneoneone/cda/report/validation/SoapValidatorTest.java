package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.config.SoapProperties;

@RunWith(MockitoJUnitRunner.class)
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

    private Element soapHeader;
    private Node to;
    private Node timestamp;
    private Node timestampCreated;
    private Node timestampExpires;
    private Node username;
    private Node replyToAddress;

    @Before
    public void setUp() {
        when(soapProperties.getSendTo()).thenReturn(VALID_SOAP_TO);
        soapHeader = mock(Element.class);
        to = mock(Node.class);
        when(to.getText()).thenReturn(VALID_SOAP_TO);
        when(soapHeader.selectSingleNode(SEND_TO_XPATH)).thenReturn(to);
        timestamp = mock(Node.class);
        when(soapHeader.selectSingleNode(TIMESTAMP_XPATH)).thenReturn(timestamp);
        timestampCreated = mock(Node.class);
        when(timestampCreated.getText()).thenReturn("2020-09-03T11:27:30Z");
        when(timestamp.selectSingleNode(TIMESTAMP_CREATED_XPATH)).thenReturn(timestampCreated);
        timestampExpires = mock(Node.class);
        when(timestampExpires.getText()).thenReturn("2020-09-04T11:27:30Z");
        when(timestamp.selectSingleNode(TIMESTAMP_EXPIRES_XPATH)).thenReturn(timestampExpires);
        username = mock(Node.class);
        when(soapHeader.selectSingleNode(USERNAME_XPATH)).thenReturn(username);
        replyToAddress = mock(Node.class);
        when(replyToAddress.getText()).thenReturn(VALID_REPLY_TO);
        when(soapHeader.selectSingleNode(REPLY_TO_ADDRESS_XPATH)).thenReturn(replyToAddress);
    }

    @Test
    public void shouldFailWhenSoapToIsInvalid() {
        String invalidTo = "InvalidTo";
        when(to.getText()).thenReturn(invalidTo);

        checkExceptionThrownAndErrorMessage("Invalid Send To value: " + invalidTo);
    }

    @Test
    public void shouldFailWhenSoapToIsMissing() {
        when(soapHeader.selectSingleNode(SEND_TO_XPATH)).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Send To missing");
    }

    @Test
    public void shouldFailWhenTimestampIsMissing() {
        when(soapHeader.selectSingleNode(SEND_TO_XPATH)).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Send To missing");
    }

    @Test
    public void shouldFailWhenTimestampIsInvalid() {
        when(timestampExpires.getText()).thenReturn("2018-09-04T11:27:30Z");

        checkExceptionThrownAndErrorMessage("Invalid timestamp");
    }

    @Test
    public void shouldFailWhenUsernameIsMissing() {
        when(soapHeader.selectSingleNode(USERNAME_XPATH)).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Username missing");
    }

    @Test
    public void shouldFailWhenReplyToInvalid() {
        String invalidReplyTo = "InvalidReplyTo";
        when(replyToAddress.getText()).thenReturn(invalidReplyTo);

        checkExceptionThrownAndErrorMessage("Invalid ReplyTo: InvalidReplyTo");
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
            fail("Unexepected exception thrown");
        }

        assertThat(exceptionThrown).isTrue();
    }
}
