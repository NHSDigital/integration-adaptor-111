package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.ListResource.ListMode.WORKING;
import static org.hl7.fhir.dstu3.model.ListResource.ListStatus.CURRENT;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListMapperTest {

    @InjectMocks
    private ListMapper listMapper;

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Mock
    private Encounter encounter;

    @Mock
    private II ii;

    private List<Resource> resourcesCreated = asList(new HealthcareService(), new QuestionnaireResponse());

    @BeforeEach
    public void setUp() {
        when(clinicalDocument.getSetId()).thenReturn(ii);
        when(ii.getRoot()).thenReturn("411910CF-1A76-4330-98FE-C345DDEE5553");
    }

    @Test
    public void shouldMapList() {
        ListResource listResource = listMapper.mapList(clinicalDocument, encounter, resourcesCreated);

        assertThat(listResource.getStatus()).isEqualTo(CURRENT);
        assertThat(listResource.getTitle()).isEqualTo("111 Report List");
        assertThat(listResource.getMode()).isEqualTo(WORKING);
        Coding orderByCode = listResource.getOrderedBy().getCodingFirstRep();
        assertThat(orderByCode.getSystem()).isEqualTo("http://hl7.org/fhir/list-order");
        assertThat(orderByCode.getCode()).isEqualTo("event-date");
        assertThat(orderByCode.getDisplay()).isEqualTo("Sorted by Event Date");
        Coding code = listResource.getCode().getCodingFirstRep();
        assertThat(code.getSystem()).isEqualTo("http://snomed.info/sct");
        assertThat(code.getCode()).isEqualTo("225390008");
        assertThat(code.getDisplay()).isEqualTo("Triage");
        assertThat(listResource.getEntry().size()).isEqualTo(1);
    }
}
