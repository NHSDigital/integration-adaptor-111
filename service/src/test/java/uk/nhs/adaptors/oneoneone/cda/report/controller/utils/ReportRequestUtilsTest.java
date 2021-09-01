package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import lombok.SneakyThrows;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.itk.envelope.DistributionEnvelopeDocument;

public class ReportRequestUtilsTest {

    private String EFFECTIVE_DATE = "20291030182905+00";
    private String TYPE_ID_EXTENSION = "POCD_HD000040";
    private int VERSION_NUMBER = 1;
    private String RECORD_TARGET_TYPE_CODE = "RCT";
    private String PATIENT_ROLE = "PAT";
    private String ADDRESS_USE = "H";

    private DistributionEnvelopeDocument distributionEnvelopeDocument;

    @InjectMocks
    private final ReportRequestUtils reportRequestUtils = new ReportRequestUtils();

    @SneakyThrows
    @BeforeEach
    private void setUp() {
        URL resource = getClass().getResource("/xml/six-clinical-docs.xml");
        distributionEnvelopeDocument = DistributionEnvelopeDocument.Factory.parse(resource);
    }

    @SneakyThrows
    @Test
    public void extractClinicalDocumentShouldReturnClinicalDocument() {

        POCDMT000002UK01ClinicalDocument1 clinicalDocumentTest = reportRequestUtils.extractClinicalDocument(distributionEnvelopeDocument);

        assertThat(clinicalDocumentTest).isNotEqualTo(null);
        assertThat(clinicalDocumentTest.getEffectiveTime().getValue()).isEqualTo(EFFECTIVE_DATE);
        assertThat(clinicalDocumentTest.getTypeId().getExtension()).isEqualTo(TYPE_ID_EXTENSION);
        assertThat(clinicalDocumentTest.getVersionNumber().getValue()).isEqualTo(VERSION_NUMBER);
        assertThat(clinicalDocumentTest.getRecordTargetArray(0).getTypeCode()).isEqualTo(RECORD_TARGET_TYPE_CODE);
        assertThat(clinicalDocumentTest.getRecordTargetArray(0).getPatientRole().getClassCode()).isEqualTo(PATIENT_ROLE);
        assertThat(clinicalDocumentTest.getRecordTargetArray(0).getPatientRole().getAddrArray(0).getUse().get(0)).isEqualTo(ADDRESS_USE);
    }
}


