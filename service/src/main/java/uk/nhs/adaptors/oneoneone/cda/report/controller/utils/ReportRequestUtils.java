package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.util.Comparator.comparing;

import static uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil.parseToInstantType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlTokenSource;
import org.dom4j.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.SneakyThrows;
import uk.nhs.adaptors.oneoneone.cda.report.controller.exceptions.ItkXmlException;
import uk.nhs.connect.iucds.cda.ucr.ClinicalDocumentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;

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

    public static POCDMT000002UK01ClinicalDocument1 extractClinicalDocument(DistributionEnvelopeDocument envelopedDocument)
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

    private static List<Node> findClinicalDocs(DistributionEnvelopeDocument envelopedDocument)
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
