package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.xmlbeans.XmlException;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.ItkXmlException;
import uk.nhs.connect.iucds.cda.ucr.ClinicalDocumentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;
import uk.nhs.itk.envelope.PayloadType;

public final class ReportRequestUtils {

    private static final String CLINICAL_DOCUMENT_NODE_NAME = "ClinicalDocument";

    public static DistributionEnvelopeDocument extractDistributionEnvelope(Element distributionEnvelope) throws ItkXmlException {
        try {
            DistributionEnvelopeDocument envelopedDocument = DistributionEnvelopeDocument.Factory.parse(distributionEnvelope.asXML());
            return envelopedDocument;
        } catch (XmlException e) {
            throw new ItkXmlException("DistributionEnvelope missing", e.getMessage(), e);
        }
    }

    public static List<POCDMT000002UK01ClinicalDocument1> extractClinicalDocument(DistributionEnvelopeDocument envelopedDocument)
        throws ItkXmlException {
        List<POCDMT000002UK01ClinicalDocument1> clinicalDocuments = new ArrayList<>();
        try {
            List<Node> nodes = findClinicalDoc(envelopedDocument);
            for (Node node:nodes) {
                clinicalDocuments.add( ClinicalDocumentDocument1.Factory
                    .parse(node)
                    .getClinicalDocument());
            }

        } catch (XmlException e) {
            throw new ItkXmlException("Clinical document missing from payload", e.getMessage(), e);
        }

        return clinicalDocuments;
    }

    private static List<Node> findClinicalDoc(DistributionEnvelopeDocument envelopedDocument)
        throws XmlException {
        PayloadType[] payloads = Arrays.stream(envelopedDocument.getDistributionEnvelope()
            .getPayloads()
            .getPayloadArray()).toArray(PayloadType[]::new);

        List<NodeList> nodeListList = new ArrayList<>();
        List<Node> nodesList = new ArrayList<>();

        for (PayloadType payload:payloads) {

            nodeListList.add(payload.getDomNode().getChildNodes());

        }

        List<Node> nodesList1 = getNodes(nodeListList, nodesList);
        if (nodesList1 != null)
            return nodesList1;
        throw new XmlException("No clinical document found in Envelope");

    }

    @Nullable
    private static List<Node> getNodes(List<NodeList> nodeListList, List<Node> nodesList) {
        for(NodeList nodeList: nodeListList) {
            Node node;
            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(i);

                if (node.getNodeName().contains(CLINICAL_DOCUMENT_NODE_NAME)) {
                    nodesList.add(node);
                }
            }
        }
        return nodesList;
    }
}
