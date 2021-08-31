package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static java.util.Arrays.asList;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.SoapClientException;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportItems;
import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.XmlUtils;

@Component
@RequiredArgsConstructor
public class ItkValidator {
    private static final String SOAP_ACTION_XPATH = "//*[local-name()='Action']";
    private static final String ITK_MANIFEST_XPATH = "//*[local-name()='manifest']";
    private static final String ITK_AUDIT_IDENTITY_XPATH = "//*[local-name()='auditIdentity']";
    private static final String ITK_MANIFEST_ITEM_XPATH = "//*[local-name()='manifestitem']";
    private static final String ITK_PAYLOAD_XPATH = "//*[local-name()='payload']";
    private static final String SOAP_VALIDATION_FAILED_MSG = "Soap validation failed";
    private static final List<String> PROFILE_IDS = asList("urn:nhs-en:profile:nhs111CDADocument-v2-0",
        "urn:nhs-en:profile:nullificationDocument-v5-0", "urn:nhs-en:profile:IntegratedUrgentCareCDADocument-v3-1",
        "urn:nhs-en:profile:nhs111CDADocument-v3-1");
    private static final String AUDIT_IDENTITY_PREFIX = "urn:nhs-uk:identity:ods:";
    private static final String AUDIT_IDENTITY_ID_XPATH = "//*[local-name()='id']";

    private final XmlUtils xmlUtils;

    public void checkItkConformance(ReportItems report) throws SoapClientException {
        checkMessageIdExists(report.getMessageId());
        checkDistributionEnvelopeExists(report.getDistributionEnvelope());
        Element itkHeader = report.getItkHeader();
        checkTrackingIdExists(itkHeader);
        checkSoapAndItkService(report.getSoapHeader(), itkHeader);
        checkPayloadAndManifest(itkHeader, report.getPayloads());
        checkAuditIdentity(itkHeader);
    }

    private void checkAuditIdentity(Element itkHeader) throws SoapClientException {
        Node auditNode = xmlUtils.getSingleNode(itkHeader, ITK_AUDIT_IDENTITY_XPATH);
        Node auditIdNode = xmlUtils.getSingleNode((Element) auditNode, AUDIT_IDENTITY_ID_XPATH);
        String auditIdentity = ((Element) auditIdNode).getAttribute("uri");

        if (!StringUtils.startsWith(auditIdentity, AUDIT_IDENTITY_PREFIX)) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Invalid Audit Identity value: " + auditIdentity);
        }
    }

    private void checkPayloadAndManifest(Element itkHeader, Element itkPayloads) throws SoapClientException {
        Element itkManifest = (Element) xmlUtils.getSingleNode(itkHeader, ITK_MANIFEST_XPATH);
        String manifestCount = itkManifest.getAttribute("count");
        String payloadCount = itkPayloads.getAttribute("count");

        if (!StringUtils.equals(manifestCount, payloadCount)) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Manifest count and payload count don't match");
        }

        List<Node> manifestItems = xmlUtils.getNodesFromElement(itkManifest, ITK_MANIFEST_ITEM_XPATH);
        checkManifestItemCount(manifestItems, manifestCount);
        List<Node> payloadItems = xmlUtils.getNodesFromElement(itkPayloads, ITK_PAYLOAD_XPATH);
        checkPayloadCount(payloadItems, payloadCount);
        checkPayloadsAndManifestsIds(manifestItems, payloadItems);
        checkManifestProfileId(manifestItems);
    }

    private void checkManifestProfileId(List<Node> manifestItems) throws SoapClientException {
        for (Node node : manifestItems) {
            Attr profileId = ((Element) node).getAttributeNode("profileid");

            if (profileId == null) {
                throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Manifest profile Id missing");
            }

            if (!PROFILE_IDS.contains(profileId.getValue())) {
                throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Invalid manifest profile Id: " + profileId.getValue());
            }
        }
    }

    private void checkPayloadsAndManifestsIds(List<Node> manifestItems, List<Node> payloadItems) throws SoapClientException {
        for (int i = 0; i < manifestItems.size(); i++) {
            String manifestItemId = ((Element) manifestItems.get(i)).getAttribute("id");
            String payloadId = ((Element) payloadItems.get(i)).getAttribute("id");

            if (!StringUtils.equals(manifestItemId, payloadId)) {
                throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Manifest and payload items IDs don't match");
            }
        }
    }

    private void checkManifestItemCount(List<Node> manifestItems, String manifestCount) throws SoapClientException {
        if (!StringUtils.equals(String.valueOf(manifestItems.size()), manifestCount)) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Manifest count attribute and manifest items size don't match");
        }
    }

    private void checkPayloadCount(List<Node> payloadItems, String payloadCount) throws SoapClientException {
        if (!StringUtils.equals(String.valueOf(payloadItems.size()), payloadCount)) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Payload count attribute and payload items size don't match");
        }
    }

    private void checkSoapAndItkService(Element soapHeader, Element itkHeader) throws SoapClientException {
        Node actionNode = xmlUtils.getSingleNode(soapHeader, SOAP_ACTION_XPATH);
        if (actionNode == null) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Action node missing");
        }

        String soapAction = actionNode.getTextContent();
        String itkService = itkHeader.getAttributeNode("service").getValue();

        if (!StringUtils.equals(soapAction, itkService)) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Soap Action is not equal to ITK service");
        }
    }

    private void checkTrackingIdExists(Element element) throws SoapClientException {
        Attr trackingid = element.getAttributeNode("trackingid");
        if (trackingid == null) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "Itk TrackingId missing");
        }
    }

    private void checkDistributionEnvelopeExists(Node element) throws SoapClientException {
        if (element == null) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "DistributionEnvelope missing");
        }
    }

    private void checkMessageIdExists(String messageId) throws SoapClientException {
        if (messageId == null) {
            throw new SoapClientException(SOAP_VALIDATION_FAILED_MSG, "MessageId missing");
        }
    }
}
