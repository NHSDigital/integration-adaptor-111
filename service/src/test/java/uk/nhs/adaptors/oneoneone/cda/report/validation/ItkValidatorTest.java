package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

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
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItems;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.XmlUtils;

@ExtendWith(MockitoExtension.class)
public class ItkValidatorTest {
    private static final String MESSAGE_ID = UUID.randomUUID().toString();
    private static final String SOAP_ADDRESS = "http://www.w3.org/2005/08/addressing/anonymous";
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

    private ReportItems reportItems;

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
    public void setUp() {
        reportItems = new ReportItems();
        reportItems.setMessageId(MESSAGE_ID);
        reportItems.setSoapAddress(SOAP_ADDRESS);
        reportItems.setDistributionEnvelope(mock(Node.class));
        reportItems.setItkHeader(prepareItkHeaderElement());
        reportItems.setSoapHeader(prepareSoapHeaderElement());
        reportItems.setPayloads(prepareItkPayloadsElement());
    }

    private Element prepareItkPayloadsElement() {
        lenient().when(itkPayloads.getAttribute("count")).thenReturn("1");
        lenient().when(itkPayload.getAttribute("id")).thenReturn("ID");
        lenient().when(xmlUtils.getNodesFromElement(itkPayloads, ITK_PAYLOAD_XPATH)).thenReturn(asList(itkPayload));

        return itkPayloads;
    }

    private Element prepareItkHeaderElement() {
        lenient().when(itkHeader.getAttributeNode("trackingid")).thenReturn(mock(Attr.class));
        lenient().when(itkService.getValue()).thenReturn(VALID_ACTION_SERVICE);
        lenient().when(itkHeader.getAttributeNode("service")).thenReturn(itkService);
        lenient().when(itkManifest.getAttribute("count")).thenReturn("1");
        lenient().when(itkManifestItem.getAttribute("id")).thenReturn("ID");
        lenient().when(itkManifestProfileId.getValue()).thenReturn(VALID_PROFILE_ID);
        lenient().when(itkManifestItem.getAttributeNode("profileid")).thenReturn(itkManifestProfileId);
        lenient().when(xmlUtils.getNodesFromElement(itkManifest, ITK_MANIFEST_ITEM_XPATH)).thenReturn(asList(itkManifestItem));
        lenient().when(xmlUtils.getSingleNode(itkHeader, ITK_MANIFEST_XPATH)).thenReturn(itkManifest);
        lenient().when(itkAuditIdentityId.getAttribute("uri")).thenReturn(VALID_AUDIT_IDENTITY);
        lenient().when(xmlUtils.getSingleNode(auditIdentity, ITK_AUDIT_IDENTITY_ID_XPATH)).thenReturn(itkAuditIdentityId);
        lenient().when(xmlUtils.getSingleNode(itkHeader, ITK_AUDIT_IDENTITY_XPATH)).thenReturn(auditIdentity);
        return itkHeader;
    }

    private Element prepareSoapHeaderElement() {
        lenient().when(xmlUtils.getSingleNode(soapHeader, SOAP_ACTION_XPATH)).thenReturn(soapAction);
        lenient().when(soapAction.getTextContent()).thenReturn(VALID_ACTION_SERVICE);

        return soapHeader;
    }

    @Test
    public void shouldFailWhenMessageIdDoesNotExist() {
        reportItems.setMessageId(null);

        checkExceptionThrownAndErrorMessage("MessageId missing");
    }

    @Test
    public void shouldFailWhenDistributionEnvelopeDoesNotExist() {
        reportItems.setDistributionEnvelope(null);

        checkExceptionThrownAndErrorMessage("DistributionEnvelope missing");
    }

    @Test
    public void shouldFailWhenTrackingIdDoesNotExist() {
        when(reportItems.getItkHeader().getAttributeNode("trackingid")).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Itk TrackingId missing");
    }

    @Test
    public void shouldFailWhenSoapActionAndItkServiceDiffer() {
        when(itkService.getValue()).thenReturn("invalidActionService");
        checkExceptionThrownAndErrorMessage("Soap Action is not equal to ITK service");
    }

    @Test
    public void shouldFailWhenManifestItemsAndCountDiffer() {
        when(xmlUtils.getNodesFromElement(itkManifest, ITK_MANIFEST_ITEM_XPATH)).thenReturn(emptyList());
        checkExceptionThrownAndErrorMessage("Manifest count attribute and manifest items size don't match");
    }

    @Test
    public void shouldFailWhenPayloadsAndCountDiffer() {
        when(xmlUtils.getNodesFromElement(itkPayloads, ITK_PAYLOAD_XPATH)).thenReturn(emptyList());
        checkExceptionThrownAndErrorMessage("Payload count attribute and payload items size don't match");
    }

    @Test
    public void shouldFailWhenInvalidProfileId() {
        when(itkManifestProfileId.getValue()).thenReturn("InvalidProfileId");
        checkExceptionThrownAndErrorMessage("Invalid manifest profile Id: InvalidProfileId");
    }

    @Test
    public void shouldFailWhenProfileIdMissing() {
        when(itkManifestItem.getAttributeNode("profileid")).thenReturn(null);
        checkExceptionThrownAndErrorMessage("Manifest profile Id missing");
    }

    @Test
    public void shouldFailWhenAuditIdentityInvalid() {
        String invalidAuditIdentity = "InvalidAuditIdentity";
        when(itkAuditIdentityId.getAttribute("uri")).thenReturn(invalidAuditIdentity);
        checkExceptionThrownAndErrorMessage("Invalid Audit Identity value: InvalidAuditIdentity");
    }

    private void checkExceptionThrownAndErrorMessage(String errorMessage) {
        boolean exceptionThrown = false;
        try {
            itkValidator.checkItkConformance(reportItems);
        } catch (SoapClientException e) {
            exceptionThrown = true;
            assertThat(e.getReason()).isEqualTo(errorMessage);
            assertThat(e.getMessage()).isEqualTo(SOAP_VALIDATION_FAILED_MSG);
        }

        assertThat(exceptionThrown).isTrue();
    }
}
