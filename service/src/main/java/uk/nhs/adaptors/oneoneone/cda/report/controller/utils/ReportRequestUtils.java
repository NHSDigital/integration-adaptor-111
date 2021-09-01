package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Component;
import org.apache.xmlbeans.XmlTokenSource;
import org.dom4j.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
        try {
            return
                findClinicalDocs(envelopedDocument).stream()
                    .map(ReportRequestUtils::parseClinicalDoc)
                    .max(comparing(doc -> {
                        return parseToInstantType(doc.getEffectiveTime().getValue()).getValue();
                    }))
                    .get();
        } catch (XmlException e) {
            throw new ItkXmlException("Clinical document missing from payload", e.getMessage(), e);
        }
    }

    @SneakyThrows
    private static POCDMT000002UK01ClinicalDocument1 parseClinicalDoc(Node node) {
        return ClinicalDocumentDocument1.Factory.parse(node).getClinicalDocument();
    }

    private Node findClinicalDoc(DistributionEnvelopeDocument envelopedDocument)
        throws XmlException {
        List<NodeList> nodeListsList = Arrays.stream(envelopedDocument.getDistributionEnvelope()
            .getPayloads()
            .getPayloadArray())
            .map(XmlTokenSource::getDomNode)
            .map(Node::getChildNodes)
            .collect(Collectors.toList());

        return getNodes(nodeListsList);
    }

    @Nullable
    private static List<Node> getNodes(List<NodeList> nodeListList) {
        return nodeListList.stream()
            .map(nodeList -> getNodeListItem(nodeList))
            .flatMap(List::stream)
            .filter(item -> item.getNodeName().contains(CLINICAL_DOCUMENT_NODE_NAME))
            .collect(Collectors.toList());
    }

    @NotNull
    private static List<Node> getNodeListItem(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
            .mapToObj(i -> nodeList.item(i)).collect(Collectors.toList());
    }
}
