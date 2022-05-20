package uk.nhs.adaptors.oneoneone.cda.report.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.mapper.QuestionnaireResponseMapper;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.ClinicalDocumentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@ExtendWith(MockitoExtension.class)
public class PathwayServiceTest {
    @InjectMocks
    private PathwayService pathwayService;
    private String pathwaysEncoded;
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private Reference patient;
    @Mock
    private Reference encounter;
    @Mock
    private NodeUtil nodeUtil;
    @Mock
    private QuestionnaireResponseMapper questionnaireResponseMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocumentMock;

    @BeforeEach
    public void setup() throws IOException, XmlException, URISyntaxException {
        String filename = (String.valueOf(Paths.get(getClass()
            .getResource("/pathways_64_bit_encoded.txt").toURI())));
        BufferedReader in = new BufferedReader(new InputStreamReader(
            new FileInputStream(filename), Charset.defaultCharset()));
        pathwaysEncoded = in.readLine();
        in.close();

        pathwayService = new PathwayService(nodeUtil, questionnaireResponseMapper);

        URL resource = getClass().getResource("/xml/example-clinical-doc.xml");
        clinicalDocument = ClinicalDocumentDocument1.Factory.parse(resource).getClinicalDocument();
    }

    @Test
    public void shouldGetPathwaysQuestionnaireResponseList() {
        when(nodeUtil.getNodeValueString(any())).thenReturn(pathwaysEncoded);

        List<QuestionnaireResponse> questionnaireResponseList = pathwayService
            .getQuestionnaireResponses(clinicalDocument, patient, encounter);

        assertFalse(questionnaireResponseList.isEmpty());
    }

    @Test
    public void shouldReturnNullWhenEmptyClinicalDocumentSent() {
        assertNull(pathwayService.getQuestionnaireResponses(clinicalDocumentMock, patient, encounter));
    }
}
