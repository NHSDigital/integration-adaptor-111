package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@RunWith(MockitoJUnitRunner.class)
public class ListMapperTest {

    @InjectMocks
    private ListMapper listMapper;

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Mock
    private Encounter encounter;

    @Mock
    private List<Resource> resourcesCreated;

    @Mock
    private II ii;

    @Before
    public void setUp() {
        when(clinicalDocument.getSetId()).thenReturn(ii);
        when(ii.getRoot()).thenReturn("411910CF-1A76-4330-98FE-C345DDEE5553");
    }

    @Test
    public void shouldMapList() {
        ListResource listResource = listMapper.mapList(clinicalDocument, encounter, resourcesCreated);

        assertThat(listResource.getStatus()).isEqualTo(ListResource.ListStatus.CURRENT);
        assertThat(listResource.getTitle()).isEqualTo("111 Report List");
        assertThat(listResource.getMode()).isEqualTo(ListResource.ListMode.WORKING);
        assertThat(listResource.getOrderedBy().getText()).isEqualTo("event-date");
        Coding code = listResource.getCode().getCodingFirstRep();
        assertThat(code.getSystem()).isEqualTo("http://snomed.info/sct");
        assertThat(code.getCode()).isEqualTo("225390008");
        assertThat(code.getDisplay()).isEqualTo("Triage");
    }
}
