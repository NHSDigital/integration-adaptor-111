package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Composition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompositionMapperTest {

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private CompositionMapper compositionMapper;

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Before
    public void setUp() {
        POCDMT000002UK01Component1 component = mock(POCDMT000002UK01Component1.class);
        when(clinicalDocument.getComponentOf()).thenReturn(component);
    }

    @Test
    public void mapComposition() {
        Composition composition = compositionMapper.mapComposition(clinicalDocument, null);
    }

}
