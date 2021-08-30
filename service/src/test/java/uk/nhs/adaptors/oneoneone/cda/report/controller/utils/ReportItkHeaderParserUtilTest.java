package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.ITK_ADDRESS_NODE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.KEY_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.SPECIFICATION_NODE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.TRACKING_ID_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.TYPE_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.URI_ATTRIBUTE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil.VALUE_ATTRIBUTE;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

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
    private XmlUtils xmlUtils;

    @Mock
    private Element headerElement;

    @Mock
    private Element specificationElement;

    @Mock
    private Element odsCodeElement;

    @Mock
    private Element dosIdElement;

    @Mock
    private Attr typeAttribute;

    @BeforeEach
    private void setUp() throws XPathExpressionException {
        when(specificationElement.getAttribute(KEY_ATTRIBUTE)).thenReturn(SPEC_KEY);
        when(specificationElement.getAttribute(VALUE_ATTRIBUTE)).thenReturn(SPEC_VALUE);
        when(headerElement.getAttribute(TRACKING_ID_ATTRIBUTE)).thenReturn(TRACKING_ID);
        when(xmlUtils.getNodesFromElement(headerElement, SPECIFICATION_NODE)).thenReturn(List.of(specificationElement));
    }

    @Test
    public void getHeaderValuesWithOdsAndDosIdShouldReturnCorrectItkReportHeader() throws XPathExpressionException {
        when(odsCodeElement.getAttribute(URI_ATTRIBUTE)).thenReturn(ODS_CODE);
        when(dosIdElement.getAttribute(URI_ATTRIBUTE)).thenReturn(DOS_ID);
        when(dosIdElement.getAttributeNode(TYPE_ATTRIBUTE)).thenReturn(typeAttribute);
        when(dosIdElement.getAttribute(TYPE_ATTRIBUTE)).thenReturn(TYPE);
        when(xmlUtils.getNodesFromElement(headerElement, ITK_ADDRESS_NODE)).thenReturn(List.of(odsCodeElement, dosIdElement));

        ItkReportHeader header = reportItkHeaderParserUtil.getHeaderValues(headerElement);

        assertThat(header.getTrackingId()).isEqualTo(TRACKING_ID);
        assertThat(header.getSpecKey()).isEqualTo(SPEC_KEY);
        assertThat(header.getSpecVal()).isEqualTo(SPEC_VALUE);
        assertThat(header.getAddressList().size()).isEqualTo(1);
        assertThat(header.getAddressList().get(0)).isEqualTo(ODS_CODE + ":DOSServiceID:" + DOS_ID);
    }

    @Test
    public void getHeaderValuesWithOdsShouldReturnCorrectItkReportHeader() throws XPathExpressionException {
        when(odsCodeElement.getAttribute(URI_ATTRIBUTE)).thenReturn(ODS_CODE);
        when(xmlUtils.getNodesFromElement(headerElement, ITK_ADDRESS_NODE)).thenReturn(List.of(odsCodeElement));

        ItkReportHeader header = reportItkHeaderParserUtil.getHeaderValues(headerElement);

        assertThat(header.getTrackingId()).isEqualTo(TRACKING_ID);
        assertThat(header.getSpecKey()).isEqualTo(SPEC_KEY);
        assertThat(header.getSpecVal()).isEqualTo(SPEC_VALUE);
        assertThat(header.getAddressList().size()).isEqualTo(1);
        assertThat(header.getAddressList().get(0)).isEqualTo(ODS_CODE);
    }

    @Test
    public void getHeaderValuesWithDosIdShouldReturnCorrectItkReportHeader() throws XPathExpressionException {
        when(dosIdElement.getAttribute(URI_ATTRIBUTE)).thenReturn(DOS_ID);
        when(dosIdElement.getAttributeNode(TYPE_ATTRIBUTE)).thenReturn(typeAttribute);
        when(dosIdElement.getAttribute(TYPE_ATTRIBUTE)).thenReturn(TYPE);
        when(xmlUtils.getNodesFromElement(headerElement, ITK_ADDRESS_NODE)).thenReturn(List.of(dosIdElement));

        ItkReportHeader header = reportItkHeaderParserUtil.getHeaderValues(headerElement);

        assertThat(header.getTrackingId()).isEqualTo(TRACKING_ID);
        assertThat(header.getSpecKey()).isEqualTo(SPEC_KEY);
        assertThat(header.getSpecVal()).isEqualTo(SPEC_VALUE);
        assertThat(header.getAddressList().size()).isEqualTo(1);
        assertThat(header.getAddressList().get(0)).isEqualTo("DOSServiceID:" + DOS_ID);
    }
}
