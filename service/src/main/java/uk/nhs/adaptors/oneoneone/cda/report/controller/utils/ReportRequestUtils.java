package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.nhs.connect.iucds.cda.ucr.ClinicalDocumentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;

public final class ReportRequestUtils {

    private static final String CLINICAL_DOCUMENT_NODE_NAME = "ClinicalDocument";

    public static POCDMT000002UK01ClinicalDocument1 extractClinicalDocument(String distributionEnvelopXml) throws XmlException {
        DistributionEnvelopeDocument envelopedDocument = DistributionEnvelopeDocument.Factory.parse(distributionEnvelopXml);
        POCDMT000002UK01ClinicalDocument1 clinicalDocument = ClinicalDocumentDocument1.Factory
            .parse(findClinicalDoc(envelopedDocument))
            .getClinicalDocument();

        return clinicalDocument;
    }

    private static Node findClinicalDoc(DistributionEnvelopeDocument envelopedDocument)
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
