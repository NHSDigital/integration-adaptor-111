package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.ITK_ADDRESS_NODE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.KEY_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.SPECIFICATION_NODE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.TRACKING_ID_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.TYPE_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.URI_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.VALUE_ATTRIBUTE;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.validation.ItkValidator;

@ExtendWith(MockitoExtension.class)
public class ReportItkHeaderParserUtilTest {
    private static final String TRACKING_ID = "5ABE57EC-7BB0-465B-89BE-62D90962D31C";
    private static final String SPEC_KEY = "urn:nhs-itk:ns:201005:interaction";
    private static final String SPEC_VALUE = "urn:nhs-itk:interaction:primaryEmergencyDepartmentRecipientNHS111";
    private static final String ODS_CODE = "urn:nhs-uk:addressing:ods:RSHSO14A";
    private static final String DOS_ID = "2000006423";
    private static final String TYPE = "2.16.840.1.113883.2.1.3.2.4.18.44";

    @InjectMocks
    private ReportItkHeaderParserUtil reportItkHeaderParserUtil;

    @Mock
    private Element headerElement;

    @Mock
    private Element specificationElement;

    @Mock
    private Element odsCodeElement;

    @Mock
    private Element dosIdElement;

    @Mock
    private Attribute typeAttribute;

    @Mock
    private ItkValidator itkValidator;

    @BeforeEach
    private void setUp() {
        when(specificationElement.attributeValue(KEY_ATTRIBUTE)).thenReturn(SPEC_KEY);
        when(specificationElement.attributeValue(VALUE_ATTRIBUTE)).thenReturn(SPEC_VALUE);
        when(headerElement.attributeValue(TRACKING_ID_ATTRIBUTE)).thenReturn(TRACKING_ID);
        when(headerElement.selectNodes(SPECIFICATION_NODE)).thenReturn(List.of(specificationElement));
    }

    @Test
    public void getHeaderValuesWithOdsAndDosIdShouldReturnCorrectItkReportHeader() throws SoapClientException {
        when(odsCodeElement.attributeValue(URI_ATTRIBUTE)).thenReturn(ODS_CODE);
        when(dosIdElement.attributeValue(URI_ATTRIBUTE)).thenReturn(DOS_ID);
        when(dosIdElement.attribute(TYPE_ATTRIBUTE)).thenReturn(typeAttribute);
        when(dosIdElement.attributeValue(TYPE_ATTRIBUTE)).thenReturn(TYPE);
        when(headerElement.selectNodes(ITK_ADDRESS_NODE)).thenReturn(List.of(odsCodeElement, dosIdElement));

        ItkReportHeader header = reportItkHeaderParserUtil.getHeaderValues(headerElement);

        assertThat(header.getTrackingId()).isEqualTo(TRACKING_ID);
        assertThat(header.getSpecKey()).isEqualTo(SPEC_KEY);
        assertThat(header.getSpecVal()).isEqualTo(SPEC_VALUE);
        assertThat(header.getAddressList().size()).isEqualTo(1);
        assertThat(header.getAddressList().get(0)).isEqualTo(ODS_CODE + ":DOSServiceID:" + DOS_ID);
    }

    @Test
    public void getHeaderValuesWithOdsShouldReturnCorrectItkReportHeader() throws SoapClientException {
        when(odsCodeElement.attributeValue(URI_ATTRIBUTE)).thenReturn(ODS_CODE);
        when(headerElement.selectNodes(ITK_ADDRESS_NODE)).thenReturn(List.of(odsCodeElement));

        ItkReportHeader header = reportItkHeaderParserUtil.getHeaderValues(headerElement);

        assertThat(header.getTrackingId()).isEqualTo(TRACKING_ID);
        assertThat(header.getSpecKey()).isEqualTo(SPEC_KEY);
        assertThat(header.getSpecVal()).isEqualTo(SPEC_VALUE);
        assertThat(header.getAddressList().size()).isEqualTo(1);
        assertThat(header.getAddressList().get(0)).isEqualTo(ODS_CODE);
    }

    @Test
    public void getHeaderValuesWithDosIdShouldReturnCorrectItkReportHeader() throws SoapClientException {
        when(dosIdElement.attributeValue(URI_ATTRIBUTE)).thenReturn(DOS_ID);
        when(dosIdElement.attribute(TYPE_ATTRIBUTE)).thenReturn(typeAttribute);
        when(dosIdElement.attributeValue(TYPE_ATTRIBUTE)).thenReturn(TYPE);
        when(headerElement.selectNodes(ITK_ADDRESS_NODE)).thenReturn(List.of(dosIdElement));

        ItkReportHeader header = reportItkHeaderParserUtil.getHeaderValues(headerElement);

        assertThat(header.getTrackingId()).isEqualTo(TRACKING_ID);
        assertThat(header.getSpecKey()).isEqualTo(SPEC_KEY);
        assertThat(header.getSpecVal()).isEqualTo(SPEC_VALUE);
        assertThat(header.getAddressList().size()).isEqualTo(1);
        assertThat(header.getAddressList().get(0)).isEqualTo("DOSServiceID:" + DOS_ID);
    }

    @Test
    public void getHeaderValuesWhenCheckItkOdsAndDosIdThrowsErrorShouldThrowError() throws SoapClientException {
        doThrow(new SoapClientException("msg", "reason")).when(itkValidator).checkItkOdsAndDosId(any(), any());

        boolean exceptionThrown = false;
        try {
            reportItkHeaderParserUtil.getHeaderValues(headerElement);
        } catch (SoapClientException e) {
            exceptionThrown = true;
            assertThat(e.getReason()).isEqualTo("reason");
            assertThat(e.getMessage()).isEqualTo("msg");
        }

        assertThat(exceptionThrown).isTrue();
    }
}
