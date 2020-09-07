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

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;
import org.junit.Before;
import org.junit.Test;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;

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

    private ItkValidator itkValidator = new ItkValidator();

    private Map<ReportElement, Element> reportMap;

    private Element itkHeader;
    private Attribute itkService;
    private Element itkManifest;
    private Attribute itkManifestCount;
    private Element itkManifestItem;
    private Attribute itkManifestItemId;
    private Attribute itkManifestProfileId;
    private Element itkPayloads;
    private Attribute itkPayloadsCount;
    private Element itkPayload;
    private Attribute itkPayloadId;
    private Element soapHeader;
    private Node soapAction;
    private Element auditIdentity;
    private Element itkAuditIdentityId;
    private Attribute itkAuditIdentityIdUri;

    @Before
    public void setUp() {
        reportMap = new HashMap<>();
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        reportMap.put(ITK_HEADER, preapreItkHeaderElement());
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());
        reportMap.put(ITK_PAYLOADS, prepareItkPayloadsElement());
    }

    private Element prepareItkPayloadsElement() {
        itkPayloads = mock(Element.class);
        itkPayloadsCount = mock(Attribute.class);
        when(itkPayloadsCount.getValue()).thenReturn("1");
        when(itkPayloads.attribute("count")).thenReturn(itkPayloadsCount);
        itkPayload = mock(Element.class);
        itkPayloadId = mock(Attribute.class);
        when(itkPayloadId.getValue()).thenReturn("ID");
        when(itkPayload.attribute("id")).thenReturn(itkPayloadId);
        when(itkPayloads.selectNodes(ITK_PAYLOAD_XPATH)).thenReturn(asList(itkPayload));

        return itkPayloads;
    }

    private Element preapreItkHeaderElement() {
        itkHeader = mock(Element.class);
        when(itkHeader.attribute("trackingid")).thenReturn(mock(Attribute.class));
        itkService = mock(Attribute.class);
        when(itkService.getValue()).thenReturn(VALID_ACTION_SERVICE);
        when(itkHeader.attribute("service")).thenReturn(itkService);
        itkManifest = mock(Element.class);
        itkManifestCount = mock(Attribute.class);
        when(itkManifestCount.getValue()).thenReturn("1");
        when(itkManifest.attribute("count")).thenReturn(itkManifestCount);
        itkManifestItem = mock(Element.class);
        itkManifestItemId = mock(Attribute.class);
        when(itkManifestItemId.getValue()).thenReturn("ID");
        when(itkManifestItem.attribute("id")).thenReturn(itkManifestItemId);
        itkManifestProfileId = mock(Attribute.class);
        when(itkManifestProfileId.getValue()).thenReturn(VALID_PROFILE_ID);
        when(itkManifestItem.attribute("profileid")).thenReturn(itkManifestProfileId);
        when(itkManifest.selectNodes(ITK_MANIFEST_ITEM_XPATH)).thenReturn(asList(itkManifestItem));
        when(itkHeader.selectSingleNode(ITK_MANIFEST_XPATH)).thenReturn(itkManifest);
        auditIdentity = mock(Element.class);
        itkAuditIdentityId = mock(Element.class);
        itkAuditIdentityIdUri = mock(Attribute.class);
        when(itkAuditIdentityIdUri.getValue()).thenReturn(VALID_AUDIT_IDENTITY);
        when(itkAuditIdentityId.attribute("uri")).thenReturn(itkAuditIdentityIdUri);
        when(auditIdentity.selectSingleNode(ITK_AUDIT_IDENTITY_ID_XPATH)).thenReturn(itkAuditIdentityId);
        when(itkHeader.selectSingleNode(ITK_AUDIT_IDENTITY_XPATH)).thenReturn(auditIdentity);
        return itkHeader;
    }

    private Element prepareSoapHeaderElement() {
        soapHeader = mock(Element.class);
        soapAction = mock(Node.class);
        when(soapHeader.selectSingleNode(SOAP_ACTION_XPATH)).thenReturn(soapAction);
        when(soapAction.getText()).thenReturn(VALID_ACTION_SERVICE);

        return soapHeader;
    }

    @Test
    public void shouldFailWhenMessageIdDoesNotExist() {
        reportMap.remove(MESSAGE_ID);

        checkExceptionThrownAndErrorMessage("MessageId missing");
    }

    @Test
    public void shouldFailWhenDistributionEnvelopeDoesNotExist() {
        reportMap.remove(DISTRIBUTION_ENVELOPE);

        checkExceptionThrownAndErrorMessage("DistributionEnvelope missing");
    }

    @Test
    public void shouldFailWhenTrackingIdDoesNotExist() {
        when(reportMap.get(ITK_HEADER).attribute("trackingid")).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Itk TrackingId missing");
    }

    @Test
    public void shouldFailWhenSoapActionIsInvalid() {
        String invalidActionService = "InvalidActionService";
        when(soapAction.getText()).thenReturn(invalidActionService);
        checkExceptionThrownAndErrorMessage("Invalid Soap Action value: " + invalidActionService);
    }

    @Test
    public void shouldFailWhenSoapActionAndItkServiceDiffer() {
        when(itkService.getValue()).thenReturn("invalidActionService");
        checkExceptionThrownAndErrorMessage("Soap Action is not equal to ITK service");
    }

    @Test
    public void shouldFailWhenManifestItemsAndCountDiffer() {
        when(itkManifest.selectNodes(ITK_MANIFEST_ITEM_XPATH)).thenReturn(emptyList());
        checkExceptionThrownAndErrorMessage("Manifest count attribute and manifest items size don't match");
    }

    @Test
    public void shouldFailWhenPayloadsAndCountDiffer() {
        when(itkPayloads.selectNodes(ITK_PAYLOAD_XPATH)).thenReturn(emptyList());
        checkExceptionThrownAndErrorMessage("Payload count attribute and payload items size don't match");
    }

    @Test
    public void shouldFailWhenInvalidProfileId() {
        when(itkManifestProfileId.getValue()).thenReturn("InvalidProfileId");
        checkExceptionThrownAndErrorMessage("Invalid manifest profile Id: InvalidProfileId");
    }

    @Test
    public void shouldFailWhenProfileIdMissing() {
        when(itkManifestItem.attribute("profileid")).thenReturn(null);
        checkExceptionThrownAndErrorMessage("Manifest profile Id missing");
    }

    @Test
    public void shouldFailWhenAuditIdentityInvalid() {
        String invalidAuditIdentity = "InvalidAuditIdentity";
        when(itkAuditIdentityIdUri.getValue()).thenReturn(invalidAuditIdentity);
        checkExceptionThrownAndErrorMessage("Invalid Audit Identity value: InvalidAuditIdentity");
    }

    private void checkExceptionThrownAndErrorMessage(String errorMessage) {
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
