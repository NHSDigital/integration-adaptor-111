package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportElement;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;

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
    private static final String ODS_CODE = "RSHSO14A";
    private static final String DOS_ID = "2000006423";
    private static final String SUPPORTED_ODS_CODES = "ABC,CDE,123";
    private static final String SUPPORTED_DOS_IDS = "1234,4321";

    @Mock
    private ItkProperties itkProperties;

    @InjectMocks
    private ItkValidator itkValidator;

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

    @BeforeEach
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
        lenient().when(itkPayloadsCount.getValue()).thenReturn("1");
        lenient().when(itkPayloads.attribute("count")).thenReturn(itkPayloadsCount);
        itkPayload = mock(Element.class);
        itkPayloadId = mock(Attribute.class);
        lenient().when(itkPayloadId.getValue()).thenReturn("ID");
        lenient().when(itkPayload.attribute("id")).thenReturn(itkPayloadId);
        lenient().when(itkPayloads.selectNodes(ITK_PAYLOAD_XPATH)).thenReturn(asList(itkPayload));

        return itkPayloads;
    }

    private Element preapreItkHeaderElement() {
        itkHeader = mock(Element.class);
        lenient().when(itkHeader.attribute("trackingid")).thenReturn(mock(Attribute.class));
        itkService = mock(Attribute.class);
        lenient().when(itkService.getValue()).thenReturn(VALID_ACTION_SERVICE);
        lenient().when(itkHeader.attribute("service")).thenReturn(itkService);
        itkManifest = mock(Element.class);
        itkManifestCount = mock(Attribute.class);
        lenient().when(itkManifestCount.getValue()).thenReturn("1");
        lenient().when(itkManifest.attribute("count")).thenReturn(itkManifestCount);
        itkManifestItem = mock(Element.class);
        itkManifestItemId = mock(Attribute.class);
        lenient().when(itkManifestItemId.getValue()).thenReturn("ID");
        lenient().when(itkManifestItem.attribute("id")).thenReturn(itkManifestItemId);
        itkManifestProfileId = mock(Attribute.class);
        lenient().when(itkManifestProfileId.getValue()).thenReturn(VALID_PROFILE_ID);
        lenient().when(itkManifestItem.attribute("profileid")).thenReturn(itkManifestProfileId);
        lenient().when(itkManifest.selectNodes(ITK_MANIFEST_ITEM_XPATH)).thenReturn(asList(itkManifestItem));
        lenient().when(itkHeader.selectSingleNode(ITK_MANIFEST_XPATH)).thenReturn(itkManifest);
        auditIdentity = mock(Element.class);
        itkAuditIdentityId = mock(Element.class);
        itkAuditIdentityIdUri = mock(Attribute.class);
        lenient().when(itkAuditIdentityIdUri.getValue()).thenReturn(VALID_AUDIT_IDENTITY);
        lenient().when(itkAuditIdentityId.attribute("uri")).thenReturn(itkAuditIdentityIdUri);
        lenient().when(auditIdentity.selectSingleNode(ITK_AUDIT_IDENTITY_ID_XPATH)).thenReturn(itkAuditIdentityId);
        lenient().when(itkHeader.selectSingleNode(ITK_AUDIT_IDENTITY_XPATH)).thenReturn(auditIdentity);
        return itkHeader;
    }

    private Element prepareSoapHeaderElement() {
        soapHeader = mock(Element.class);
        soapAction = mock(Node.class);
        lenient().when(soapHeader.selectSingleNode(SOAP_ACTION_XPATH)).thenReturn(soapAction);
        lenient().when(soapAction.getText()).thenReturn(VALID_ACTION_SERVICE);

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

    @Test
    public void shouldFailWhenOdsAndDosIdAreNotSupported() {
        String expectedMessage = String.format("Both ODS code (%s) and DOS ID (%s) are invalid", ODS_CODE, DOS_ID);
        String expectedReason = "Message rejected";
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS);

        boolean exceptionThrown = false;
        try {
            itkValidator.checkItkOdsAndDosId("urn:nhs-uk:addressing:ods:" + ODS_CODE, "DOSServiceID:" + DOS_ID);
        } catch (SoapClientException e) {
            exceptionThrown = true;
            assertThat(e.getReason()).isEqualTo(expectedReason);
            assertThat(e.getMessage()).isEqualTo(expectedMessage);
        }

        assertThat(exceptionThrown).isTrue();
    }

    @Test
    public void shouldNotFailWhenOdsIsSupported() {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES + "," + ODS_CODE);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS);

        checkExceptionNotThrown();
    }

    @Test
    public void shouldNotFailWhenDosIdIsSupported() {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS + "," + DOS_ID);

        checkExceptionNotThrown();
    }

    @Test
    public void shouldNotFailWhenOdsAndDosIdAreSupported() {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES + "," + ODS_CODE);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS + "," + DOS_ID);

        checkExceptionNotThrown();
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

    private void checkExceptionNotThrown() {
        boolean exceptionThrown = false;
        try {
            itkValidator.checkItkOdsAndDosId("urn:nhs-uk:addressing:ods:" + ODS_CODE, "DOSServiceID:" + DOS_ID);
        } catch (SoapClientException e) {
            exceptionThrown = true;
        }

        assertThat(exceptionThrown).isFalse();
    }
}
