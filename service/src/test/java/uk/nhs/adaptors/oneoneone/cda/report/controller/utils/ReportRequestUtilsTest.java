package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static java.nio.charset.Charset.defaultCharset;
import static java.nio.file.Files.readAllBytes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Node;

import lombok.SneakyThrows;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;

@ExtendWith(MockitoExtension.class)
public class ReportRequestUtilsTest {
    private static final String EFFECTIVE_DATE = "20201030182905+00";
    private static final String TYPE_ID_EXTENSION = "POCD_HD000040";
    private static final int VERSION_NUMBER = 1;
    private static final String RECORD_TARGET_TYPE_CODE = "RCT";
    private static final String PATIENT_ROLE = "PAT";
    private static final String ADDRESS_USE = "H";
    private static final int PAYLOADS_COUNT = 6;
    private static final String MANIFEST_ID = "uuid_A25CDB08-9AE9-483D-9833-D78A6E40D0AF";
    private static final String CLINICAL_DOCUMENT_NODE_NAME = "ClinicalDocument";
    private static final String REPEAT_CALLER_XML = getRepeatCallerXml();

    @Mock
    private Node distributionEnvelope;

    @Mock
    private XmlUtils xmlUtils;

    @InjectMocks
    private ReportRequestUtils reportRequestUtils;
    private DistributionEnvelopeDocument distributionEnvelopeDocument;

    @SneakyThrows
    @Test
    public void extractDistributionEnvelopeShouldReturnDistributionEnvelopeDocument() {
        when(xmlUtils.serialize(distributionEnvelope)).thenReturn(REPEAT_CALLER_XML);
        DistributionEnvelopeDocument distributionEnvelopeDocumentTest =
            reportRequestUtils.extractDistributionEnvelope(distributionEnvelope);

        assertThat(distributionEnvelopeDocumentTest).isNotEqualTo(null);
        assertThat(distributionEnvelopeDocumentTest.getDistributionEnvelope().getPayloads().getCount()).isEqualTo(PAYLOADS_COUNT);
        assertThat(distributionEnvelopeDocumentTest.getDistributionEnvelope().getHeader().getManifest().getManifestitemArray(0).getId())
            .isEqualTo(MANIFEST_ID);
        assertThat(distributionEnvelopeDocumentTest.getDistributionEnvelope().getPayloads()
            .getPayloadArray(0).getDomNode().getChildNodes().item(1).getNodeName())
            .isEqualTo(CLINICAL_DOCUMENT_NODE_NAME);
    }

    @SneakyThrows
    @Test
    public void extractClinicalDocumentShouldReturnClinicalDocument() {

        distributionEnvelopeDocument = DistributionEnvelopeDocument.Factory.parse(REPEAT_CALLER_XML);

        POCDMT000002UK01ClinicalDocument1 clinicalDocumentTest = reportRequestUtils.extractClinicalDocument(distributionEnvelopeDocument);

        assertThat(clinicalDocumentTest).isNotEqualTo(null);
        assertThat(clinicalDocumentTest.getEffectiveTime().getValue()).isEqualTo(EFFECTIVE_DATE);
        assertThat(clinicalDocumentTest.getTypeId().getExtension()).isEqualTo(TYPE_ID_EXTENSION);
        assertThat(clinicalDocumentTest.getVersionNumber().getValue()).isEqualTo(VERSION_NUMBER);
        assertThat(clinicalDocumentTest.getRecordTargetArray(0).getTypeCode()).isEqualTo(RECORD_TARGET_TYPE_CODE);
        assertThat(clinicalDocumentTest.getRecordTargetArray(0).getPatientRole().getClassCode()).isEqualTo(PATIENT_ROLE);
        assertThat(clinicalDocumentTest.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getUse().get(0)).isEqualTo(ADDRESS_USE);
    }

    @SneakyThrows
    private static String getRepeatCallerXml() {
        URL reportXmlResource = ReportRequestUtilsTest.class.getResource("/xml/repeatCallerDistributionEnvelope.xml");
        return new String(readAllBytes(Paths.get(reportXmlResource.getPath())), defaultCharset());
    }
}


