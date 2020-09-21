package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URL;

import org.apache.xmlbeans.XmlException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.connect.iucds.cda.ucr.ClinicalDocumentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

@RunWith(MockitoJUnitRunner.class)
public class StructuredBodyUtilTest {
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocumentMock;

    @Before
    public void setup() throws IOException, XmlException {
        URL resource = getClass().getResource("/xml/example-clinical-doc.xml");
        clinicalDocument = ClinicalDocumentDocument1.Factory.parse(resource).getClinicalDocument();
    }

    @Test
    public void shouldReturnStructuredBody() {
        POCDMT000002UK01StructuredBody structuredBody = StructuredBodyUtil.getStructuredBody(clinicalDocument);
        assertThat(structuredBody).isNotEqualTo(null);
    }

    @Test
    public void shouldReturnNullWhenEmptyClinicalDocumentSent() {
        assertNull(StructuredBodyUtil.getStructuredBody(clinicalDocumentMock));
    }
}
