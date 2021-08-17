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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItkHeaderParserUtil;
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
    private static final String SUPPORTED_ODS_CODE = "ABC";
    private static final String SUPPORTED_DOS_ID = "1234";
    private static final String NOT_SUPPORTED_ODS_CODE = "RSHSO14A";
    private static final String NOT_SUPPORTED_DOS_ID = "2000006423";
    private static final List<String> SUPPORTED_ODS_CODES = Arrays.asList("ABC","CDE","123");
    private static final List<String> SUPPORTED_DOS_IDS = Arrays.asList("1234","4321");

    @Mock
    private ItkProperties itkProperties;

    @Mock
    private ReportItkHeaderParserUtil reportItkHeaderParserUtil;

    @InjectMocks
    private ItkValidator itkValidator;

    private Map<ReportElement, Element> reportMap = new HashMap<>();

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

//    @BeforeEach
    public void setUp() {
//        reportMap = new HashMap<>();
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        reportMap.put(ITK_HEADER, prepareItkHeaderElement());
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

    private Element prepareItkHeaderElement() {
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
//        reportMap.remove(MESSAGE_ID);

        checkExceptionThrownAndErrorMessage("MessageId missing", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenDistributionEnvelopeDoesNotExist() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
//        reportMap.remove(DISTRIBUTION_ENVELOPE);

        checkExceptionThrownAndErrorMessage("DistributionEnvelope missing", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenTrackingIdDoesNotExist() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        itkHeader = mock(Element.class);
        reportMap.put(ITK_HEADER, itkHeader);
        when(reportMap.get(ITK_HEADER).attribute("trackingid")).thenReturn(null);

        checkExceptionThrownAndErrorMessage("Itk TrackingId missing", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenSoapActionAndItkServiceDiffer() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        itkHeader = mock(Element.class);
        reportMap.put(ITK_HEADER, itkHeader);
        when(itkHeader.attribute("trackingid")).thenReturn(mock(Attribute.class));
        itkService = mock(Attribute.class);
        when(itkService.getValue()).thenReturn("invalidActionService");
        when(itkHeader.attribute("service")).thenReturn(itkService);
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());

        checkExceptionThrownAndErrorMessage("Soap Action is not equal to ITK service", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenManifestItemsAndCountDiffer() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        itkHeader = mock(Element.class);
        reportMap.put(ITK_HEADER, itkHeader);
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
        when(itkHeader.selectSingleNode(ITK_MANIFEST_XPATH)).thenReturn(itkManifest);
        when(itkManifest.selectNodes(ITK_MANIFEST_ITEM_XPATH)).thenReturn(emptyList());
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());
        checkExceptionThrownAndErrorMessage("Manifest count attribute and manifest items size don't match", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenPayloadsAndCountDiffer() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        itkHeader = mock(Element.class);
        reportMap.put(ITK_HEADER, itkHeader);
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
        when(itkHeader.selectSingleNode(ITK_MANIFEST_XPATH)).thenReturn(itkManifest);
        when(itkManifest.selectNodes(ITK_MANIFEST_ITEM_XPATH)).thenReturn(asList(itkManifestItem));

        itkPayloads = mock(Element.class);
        itkPayloadsCount = mock(Attribute.class);
        when(itkPayloadsCount.getValue()).thenReturn("1");
        when(itkPayloads.attribute("count")).thenReturn(itkPayloadsCount);
        itkPayload = mock(Element.class);
        itkPayloadId = mock(Attribute.class);
        when(itkPayloadId.getValue()).thenReturn("ID");
        when(itkPayload.attribute("id")).thenReturn(itkPayloadId);
        when(itkPayloads.selectNodes(ITK_PAYLOAD_XPATH)).thenReturn(emptyList());
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());
        checkExceptionThrownAndErrorMessage("Payload count attribute and payload items size don't match", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenInvalidProfileId() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        itkHeader = mock(Element.class);
        reportMap.put(ITK_HEADER, itkHeader);
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
//        when(itkManifestProfileId.getValue()).thenReturn(VALID_PROFILE_ID);
        when(itkManifestItem.attribute("profileid")).thenReturn(itkManifestProfileId);
        when(itkManifest.selectNodes(ITK_MANIFEST_ITEM_XPATH)).thenReturn(asList(itkManifestItem));
        when(itkHeader.selectSingleNode(ITK_MANIFEST_XPATH)).thenReturn(itkManifest);

        itkPayloads = mock(Element.class);
        itkPayloadsCount = mock(Attribute.class);
        when(itkPayloadsCount.getValue()).thenReturn("1");
        when(itkPayloads.attribute("count")).thenReturn(itkPayloadsCount);
        itkPayload = mock(Element.class);
        itkPayloadId = mock(Attribute.class);
        when(itkPayloadId.getValue()).thenReturn("ID");
        when(itkPayload.attribute("id")).thenReturn(itkPayloadId);
        when(itkPayloads.selectNodes(ITK_PAYLOAD_XPATH)).thenReturn(asList(itkPayload));

        when(itkManifestProfileId.getValue()).thenReturn("InvalidProfileId");
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());
        checkExceptionThrownAndErrorMessage("Invalid manifest profile Id: InvalidProfileId", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenProfileIdMissing() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        itkHeader = mock(Element.class);
        reportMap.put(ITK_HEADER, itkHeader);
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
        //        when(itkManifestProfileId.getValue()).thenReturn(VALID_PROFILE_ID);
//        when(itkManifestItem.attribute("profileid")).thenReturn(itkManifestProfileId);
        when(itkManifest.selectNodes(ITK_MANIFEST_ITEM_XPATH)).thenReturn(asList(itkManifestItem));
        when(itkHeader.selectSingleNode(ITK_MANIFEST_XPATH)).thenReturn(itkManifest);

        itkPayloads = mock(Element.class);
        itkPayloadsCount = mock(Attribute.class);
        when(itkPayloadsCount.getValue()).thenReturn("1");
        when(itkPayloads.attribute("count")).thenReturn(itkPayloadsCount);
        itkPayload = mock(Element.class);
        itkPayloadId = mock(Attribute.class);
        when(itkPayloadId.getValue()).thenReturn("ID");
        when(itkPayload.attribute("id")).thenReturn(itkPayloadId);
        when(itkPayloads.selectNodes(ITK_PAYLOAD_XPATH)).thenReturn(asList(itkPayload));
        when(itkManifestItem.attribute("profileid")).thenReturn(null);
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());
        checkExceptionThrownAndErrorMessage("Manifest profile Id missing", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenAuditIdentityInvalid() {
        reportMap.put(MESSAGE_ID, mock(Element.class));
        reportMap.put(DISTRIBUTION_ENVELOPE, mock(Element.class));
        itkHeader = mock(Element.class);
        reportMap.put(ITK_HEADER, itkHeader);
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

        itkPayloads = mock(Element.class);
        itkPayloadsCount = mock(Attribute.class);
        when(itkPayloadsCount.getValue()).thenReturn("1");
        when(itkPayloads.attribute("count")).thenReturn(itkPayloadsCount);
        itkPayload = mock(Element.class);
        itkPayloadId = mock(Attribute.class);
        when(itkPayloadId.getValue()).thenReturn("ID");
        when(itkPayload.attribute("id")).thenReturn(itkPayloadId);
        when(itkPayloads.selectNodes(ITK_PAYLOAD_XPATH)).thenReturn(asList(itkPayload));
        String invalidAuditIdentity = "InvalidAuditIdentity";
        itkAuditIdentityIdUri = mock(Attribute.class);
        itkAuditIdentityId = mock(Element.class);
        //        when(itkAuditIdentityIdUri.getValue()).thenReturn(VALID_AUDIT_IDENTITY);
        when(itkAuditIdentityId.attribute("uri")).thenReturn(itkAuditIdentityIdUri);
        when(itkAuditIdentityIdUri.getValue()).thenReturn(invalidAuditIdentity);
        reportMap.put(SOAP_HEADER, prepareSoapHeaderElement());
        checkExceptionThrownAndErrorMessage("Invalid Audit Identity value: InvalidAuditIdentity", SOAP_VALIDATION_FAILED_MSG);
    }

    @Test
    public void shouldFailWhenOdsAndDosIdAreNotSupported() {
        setUp();
        String expectedMessage = String.format("Both ODS code (%s) and DOS ID (%s) are invalid", NOT_SUPPORTED_ODS_CODE, NOT_SUPPORTED_DOS_ID);
        String expectedReason = "Message rejected";
        prepareItkPropertiesMock();
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(NOT_SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(NOT_SUPPORTED_DOS_ID);

        checkExceptionThrownAndErrorMessage(expectedReason, expectedMessage);
    }

    @Test
    public void shouldNotFailWhenOdsIsSupported() {
        setUp();
        prepareItkPropertiesMock();
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(NOT_SUPPORTED_DOS_ID);

        checkExceptionNotThrown();
    }

    @Test
    public void shouldNotFailWhenDosIdIsSupported() {
        setUp();
        prepareItkPropertiesMock();
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(NOT_SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(SUPPORTED_DOS_ID);

        checkExceptionNotThrown();
    }

    @Test
    public void shouldNotFailWhenOdsAndDosIdAreSupported() {
        setUp();
        prepareItkPropertiesMock();
        when(reportItkHeaderParserUtil.getOdsCode(itkHeader)).thenReturn(SUPPORTED_ODS_CODE);
        when(reportItkHeaderParserUtil.getDosServiceId(itkHeader)).thenReturn(SUPPORTED_DOS_ID);

        checkExceptionNotThrown();
    }

    private void checkExceptionThrownAndErrorMessage(String reason, String errorMessage) {
        boolean exceptionThrown = false;
        try {
            itkValidator.checkItkConformance(reportMap);
        } catch (SoapClientException e) {
            exceptionThrown = true;
            assertThat(e.getReason()).isEqualTo(reason);
            assertThat(e.getMessage()).isEqualTo(errorMessage);
        }

        assertThat(exceptionThrown).isTrue();
    }

    private void checkExceptionNotThrown() {
        boolean exceptionThrown = false;
        try {
            itkValidator.checkItkConformance(reportMap);
        } catch (SoapClientException e) {
            exceptionThrown = true;
        }

        assertThat(exceptionThrown).isFalse();
    }

    private void prepareItkPropertiesMock() {
        when(itkProperties.getOdsCodes()).thenReturn(SUPPORTED_ODS_CODES);
        when(itkProperties.getDosIds()).thenReturn(SUPPORTED_DOS_IDS);
    }
}
