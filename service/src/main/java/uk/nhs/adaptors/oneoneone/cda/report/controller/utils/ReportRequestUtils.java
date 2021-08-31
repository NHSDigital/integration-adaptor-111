package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.ItkXmlException;
import uk.nhs.connect.iucds.cda.ucr.ClinicalDocumentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;

@Component
@RequiredArgsConstructor
public class ReportRequestUtils {

    private static final String CLINICAL_DOCUMENT_NODE_NAME = "ClinicalDocument";

    private final XmlUtils xmlUtils;

    @SneakyThrows
    public DistributionEnvelopeDocument extractDistributionEnvelope(Node distributionEnvelope) throws ItkXmlException {
        try {
            return DistributionEnvelopeDocument.Factory.parse(xmlUtils.serialize(distributionEnvelope));
        } catch (XmlException e) {
            throw new ItkXmlException("DistributionEnvelope missing", e.getMessage(), e);
        }
    }

    public POCDMT000002UK01ClinicalDocument1 extractClinicalDocument(DistributionEnvelopeDocument envelopedDocument)
        throws ItkXmlException {
        POCDMT000002UK01ClinicalDocument1 clinicalDocument;
        try {
            clinicalDocument = ClinicalDocumentDocument1.Factory
                .parse(findClinicalDoc(envelopedDocument))
                .getClinicalDocument();
        } catch (XmlException e) {
            throw new ItkXmlException("Clinical document missing from payload", e.getMessage(), e);
        }

        return clinicalDocument;
    }

    private Node findClinicalDoc(DistributionEnvelopeDocument envelopedDocument)
        throws XmlException {
        NodeList childNodes = envelopedDocument.getDistributionEnvelope()
            .getPayloads()
            .getPayloadArray(0)
            .getDomNode()
            .getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node.getNodeName().contains(CLINICAL_DOCUMENT_NODE_NAME)) {
                return node;
            }
        }
        throw new XmlException("No clinical document found in Envelope");
    }
}
