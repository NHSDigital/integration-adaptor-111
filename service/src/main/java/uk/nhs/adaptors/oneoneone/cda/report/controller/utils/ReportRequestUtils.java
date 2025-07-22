package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.util.Comparator.comparing;

import static uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil.parseToInstantType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlTokenSource;
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

    public DistributionEnvelopeDocument extractDistributionEnvelope(Node distributionEnvelope) throws ItkXmlException {
        try {
            return (DistributionEnvelopeDocument) XmlObject.Factory.parse(distributionEnvelope);
        } catch (XmlException e) {
            throw new ItkXmlException("DistributionEnvelope missing", e.getMessage(), e);
        }
    }

    public POCDMT000002UK01ClinicalDocument1 extractClinicalDocument(DistributionEnvelopeDocument envelopedDocument)
        throws ItkXmlException {
        return
            findClinicalDocs(envelopedDocument).stream()
                .map(ReportRequestUtils::parseClinicalDoc)
                .max(comparing(doc -> parseToInstantType(doc.getEffectiveTime().getValue()).getValue()))
                .orElseThrow(() -> new ItkXmlException("ClinicalDocument missing", "Unable to find ClinicalDocument element"));
    }

    @SneakyThrows
    private static POCDMT000002UK01ClinicalDocument1 parseClinicalDoc(Node node) {
        var clinicalDocumentDocument = (ClinicalDocumentDocument1) XmlObject.Factory.parse(node);
        return clinicalDocumentDocument.getClinicalDocument();
    }

    private List<Node> findClinicalDocs(DistributionEnvelopeDocument envelopedDocument) {
        List<NodeList> nodeListsList = Arrays.stream(envelopedDocument.getDistributionEnvelope()
            .getPayloads()
            .getPayloadArray())
            .map(XmlTokenSource::getDomNode)
            .map(Node::getChildNodes)
            .collect(Collectors.toList());

        return getNodes(nodeListsList);
    }

    private static List<Node> getNodes(List<NodeList> nodeListList) {
        return nodeListList.stream()
            .map(nodeList -> getNodeListItem(nodeList))
            .flatMap(List::stream)
            .filter(item -> item.getNodeName().contains(CLINICAL_DOCUMENT_NODE_NAME))
            .collect(Collectors.toList());
    }

    private static List<Node> getNodeListItem(NodeList nodeList) {
        return IntStream.range(0, nodeList.getLength())
            .mapToObj(i -> nodeList.item(i)).collect(Collectors.toList());
    }
}
