package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;

@ExtendWith(MockitoExtension.class)
public class ItkAddressValidatorTest {
    private static final String SUPPORTED_ODS_CODE = "ABC";
    private static final String SUPPORTED_DOS_ID = "1234";
    private static final String NOT_SUPPORTED_ODS_CODE = "RSHSO14A";
    private static final String NOT_SUPPORTED_DOS_ID = "2000006423";
    private static final List<String> SUPPORTED_ODS_CODES = Arrays.asList("ABC", "CDE", "123");
    private static final List<String> SUPPORTED_DOS_IDS = Arrays.asList("1234", "4321");

    private final Element itkHeader = mock(Element.class);

    @Mock
    private ItkProperties itkProperties;

    @Mock
    private ReportItkHeaderParserUtil reportItkHeaderParserUtil;

    @InjectMocks
    private ItkAddressValidator itkAddressValidator;

    @Test
    public void shouldFailWhenOdsAndDosIdAreNotSupported() throws XPathExpressionException {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS);
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(NOT_SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(NOT_SUPPORTED_DOS_ID);

        checkExceptionThrown(itkHeader);
    }

    private void checkExceptionThrown(Element header) throws XPathExpressionException {
        String expectedMessage = String.format("Both ODS code (%s) and DOS ID (%s) are invalid",
            NOT_SUPPORTED_ODS_CODE, NOT_SUPPORTED_DOS_ID);
        String expectedReason = "Message rejected";

        boolean exceptionThrown = false;
        try {
            itkAddressValidator.checkItkOdsAndDosId(header);
        } catch (SoapClientException e) {
            exceptionThrown = true;
            assertThat(e.getReason()).isEqualTo(expectedReason);
            assertThat(e.getMessage()).isEqualTo(expectedMessage);
        }

        assertThat(exceptionThrown).isTrue();
    }

    @Test
    public void shouldNotFailWhenOdsIsSupported() throws XPathExpressionException {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS);
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(NOT_SUPPORTED_DOS_ID);

        checkExceptionNotThrown();
    }

    @Test
    public void shouldFailWhenOdsNotSupportedAndDosIdEmpty() throws XPathExpressionException {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(newArrayList());
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(NOT_SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(NOT_SUPPORTED_DOS_ID);

        checkExceptionThrown(itkHeader);
    }

    @Test
    public void shouldNotFailWhenBothOdsAndDosListNotDefined() throws XPathExpressionException {
        when(itkProperties.getOdsCodes()).thenReturn(newArrayList());
        when(itkProperties.getDosIds()).thenReturn(newArrayList());

        checkExceptionNotThrown();
    }

    @Test
    public void shouldNotFailWhenDosIdIsSupported() throws XPathExpressionException {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS);
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(NOT_SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(SUPPORTED_DOS_ID);

        checkExceptionNotThrown();
    }

    @Test
    public void shouldNotFailWhenOdsAndDosIdAreSupported() throws XPathExpressionException {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS);
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(SUPPORTED_DOS_ID);

        checkExceptionNotThrown();
    }

    private void checkExceptionNotThrown() throws XPathExpressionException {
        boolean exceptionThrown = false;
        try {
            itkAddressValidator.checkItkOdsAndDosId(itkHeader);
        } catch (SoapClientException e) {
            exceptionThrown = true;
        }

        assertThat(exceptionThrown).isFalse();
    }
}
