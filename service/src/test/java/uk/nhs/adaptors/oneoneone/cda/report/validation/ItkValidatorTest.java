package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.DISTRIBUTION_ENVELOPE;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.ITK_HEADER;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.ITK_PAYLOADS;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.MESSAGE_ID;
import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement.SOAP_HEADER;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.XmlUtils;

@ExtendWith(MockitoExtension.class)
public class ItkValidatorTest {
    private static final String VALID_ACTION_SERVICE = "urn:nhs-itk:services:201005:SendNHS111Report-v2-0";
    private static final String SOAP_ACTION_XPATH = "//*[local-name()='Action']";
    private static final String ITK_MANIFEST_XPATH = "//*[local-name()='manifest']";
    private static final String ITK_MANIFEST_ITEM_XPATH = "//*[local-name()='manifestitem']";
    private static final String ITK_PAYLOAD_XPATH = "//*[local-name()='payload']";
    private static final String ITK_AUDIT_IDENTITY_XPATH = "//*[local-name()='auditIdentity']";
    private static final String ITK_AUDIT_IDENTITY_ID_XPATH = "//*[local-name()='id']";
    private static final String SOAP_VALIDATION_FAILED_MSG = "Soap validation failed";
    private static final String VALID_PROFILE_ID = "urn:nhs-en:profile:nhs111CDADocument-v2-0";
    private static final String VALID_AUDIT_IDENTITY = "urn:nhs-uk:identity:ods:5L399";

    @Mock
    private XmlUtils xmlUtils;

    private Map<ReportElement, Element> reportMap;

    @Mock
    private Element itkHeader;
    @Mock
    private Attr itkService;
    @Mock
    private Element itkManifest;
    @Mock
    private Element itkManifestItem;
    @Mock
    private Attr itkManifestProfileId;
    @Mock
    private Element itkPayloads;
    @Mock
    private Element itkPayload;
    @Mock
    private Element soapHeader;
    @Mock
    private Node soapAction;
    @Mock
    private Element auditIdentity;
    @Mock
    private Element itkAuditIdentityId;

    @InjectMocks
    private ItkValidator itkValidator;

    @BeforeEach
    public void setUp() throws XPathExpressionException {
        reportMap = new HashMap<>();
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        reportMap.put(ITK_HEADER, prepareItkHeaderElement());
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());
        reportMap.put(ITK_PAYLOADS, prepareItkPayloadsElement());
    }

    private Element prepareItkPayloadsElement() throws XPathExpressionException {
        when(itkPayloads.getAttribute("count")).thenReturn("1");
        when(itkPayload.getAttribute("id")).thenReturn("ID");
        when(xmlUtils.getNodesFromElement(itkPayloads, ITK_PAYLOAD_XPATH)).thenReturn(asList(itkPayload));

        return itkPayloads;
    }

    private Element prepareItkHeaderElement() throws XPathExpressionException {
        when(itkHeader.getAttributeNode("trackingid")).thenReturn(mock(Attr.class));
        when(itkService.getValue()).thenReturn(VALID_ACTION_SERVICE);
        when(itkHeader.getAttributeNode("service")).thenReturn(itkService);
        when(itkManifest.getAttribute("count")).thenReturn("1");
        when(itkManifestItem.getAttribute("id")).thenReturn("ID");
        when(itkManifestProfileId.getValue()).thenReturn(VALID_PROFILE_ID);
        when(itkManifestItem.getAttributeNode("profileid")).thenReturn(itkManifestProfileId);
        when(xmlUtils.getNodesFromElement(itkManifest, ITK_MANIFEST_ITEM_XPATH)).thenReturn(asList(itkManifestItem));
        when(xmlUtils.getSingleNode(itkHeader, ITK_MANIFEST_XPATH)).thenReturn(itkManifest);
        when(itkAuditIdentityId.getAttribute("uri")).thenReturn(VALID_AUDIT_IDENTITY);
        when(xmlUtils.getSingleNode(auditIdentity, ITK_AUDIT_IDENTITY_ID_XPATH)).thenReturn(itkAuditIdentityId);
        when(xmlUtils.getSingleNode(itkHeader, ITK_AUDIT_IDENTITY_XPATH)).thenReturn(auditIdentity);
        return itkHeader;
    }

    private Element prepareSoapHeaderElement() throws XPathExpressionException {
        when(xmlUtils.getSingleNode(soapHeader, SOAP_ACTION_XPATH)).thenReturn(soapAction);
        when(soapAction.getNodeValue()).thenReturn(VALID_ACTION_SERVICE);

        return soapHeader;
    }

    @Test
    public void shouldFailWhenMessageIdDoesNotExist() throws XPathExpressionException {
        reportMap.remove(MESSAGE_ID);

        checkExceptionThrownAndErrorMessage("MessageId missing");
    }

    @Test
    public void shouldFailWhenDistributionEnvelopeDoesNotExist() throws XPathExpressionException {
        reportMap.remove(DISTRIBUTION_ENVELOPE);

        checkExceptionThrownAndErrorMessage("DistributionEnvelope missing");
    }

    @Test
    public void shouldFailWhenTrackingIdDoesNotExist() throws XPathExpressionException {
        when(reportMap.get(ITK_HEADER).getAttributeNode("trackingid")).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Itk TrackingId missing");
    }

    @Test
    public void shouldFailWhenSoapActionAndItkServiceDiffer() throws XPathExpressionException {
        when(itkService.getValue()).thenReturn("invalidActionService");
        checkExceptionThrownAndErrorMessage("Soap Action is not equal to ITK service");
    }

    @Test
    public void shouldFailWhenManifestItemsAndCountDiffer() throws XPathExpressionException {
        when(xmlUtils.getNodesFromElement(itkManifest, ITK_MANIFEST_ITEM_XPATH)).thenReturn(emptyList());
        checkExceptionThrownAndErrorMessage("Manifest count attribute and manifest items size don't match");
    }

    @Test
    public void shouldFailWhenPayloadsAndCountDiffer() throws XPathExpressionException {
        when(xmlUtils.getNodesFromElement(itkPayloads, ITK_PAYLOAD_XPATH)).thenReturn(emptyList());
        checkExceptionThrownAndErrorMessage("Payload count attribute and payload items size don't match");
    }

    @Test
    public void shouldFailWhenInvalidProfileId() throws XPathExpressionException {
        when(itkManifestProfileId.getValue()).thenReturn("InvalidProfileId");
        checkExceptionThrownAndErrorMessage("Invalid manifest profile Id: InvalidProfileId");
    }

    @Test
    public void shouldFailWhenProfileIdMissing() throws XPathExpressionException {
        when(itkManifestItem.getAttributeNode("profileid")).thenReturn(null);
        checkExceptionThrownAndErrorMessage("Manifest profile Id missing");
    }

    @Test
    public void shouldFailWhenAuditIdentityInvalid() throws XPathExpressionException {
        String invalidAuditIdentity = "InvalidAuditIdentity";
        when(itkAuditIdentityId.getAttribute("uri")).thenReturn(invalidAuditIdentity);
        checkExceptionThrownAndErrorMessage("Invalid Audit Identity value: InvalidAuditIdentity");
    }

    private void checkExceptionThrownAndErrorMessage(String errorMessage) throws XPathExpressionException {
        boolean exceptionThrown = false;
        try {
            itkValidator.checkItkConformance(reportMap);
        } catch (SoapClientException e) {
            exceptionThrown = true;
            assertThat(e.getReason()).isEqualTo(errorMessage);
            assertThat(e.getMessage()).isEqualTo(SOAP_VALIDATION_FAILED_MSG);
        }

        assertThat(exceptionThrown).isTrue();
    }
}
