package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedDocument1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompositionMapperTest {

    private final IBaseResource episodeOfCareRefResource = new EpisodeOfCare();
    private final CarePlan carePlan = new CarePlan();
    private final List<CarePlan> carePlans = Arrays.asList(carePlan);
    @InjectMocks
    private CompositionMapper compositionMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private POCDMT000002UK01RelatedDocument1 relatedDocument1;
    @Mock
    private POCDMT000002UK01ParentDocument1 parentDocument1;
    @Mock
    private II ii;
    @Mock
    private CE ce;
    @Mock
    private Encounter encounter;
    @Mock
    private Reference episodeOfCareRef;
    @Mock
    private Reference reference;
    @Mock
    private ReferralRequest referralRequest;
    @Mock
    private POCDMT000002UK01Component2 component2;

    @BeforeEach
    public void setUp() {
        POCDMT000002UK01RelatedDocument1[] relatedDocsArray = {mock(POCDMT000002UK01RelatedDocument1.class)};
        when(clinicalDocument.getRelatedDocumentArray()).thenReturn(relatedDocsArray);
        when(clinicalDocument.getRelatedDocumentArray(0)).thenReturn(relatedDocument1);
        when(relatedDocument1.getParentDocument()).thenReturn(parentDocument1);
        when(parentDocument1.getIdArray(0)).thenReturn(ii);
        when(clinicalDocument.getSetId()).thenReturn(ii);
        when(ii.getRoot()).thenReturn("411910CF-1A76-4330-98FE-C345DDEE5553");
        when(clinicalDocument.getConfidentialityCode()).thenReturn(ce);
        when(ce.getCode()).thenReturn("V");
        when(encounter.getEpisodeOfCareFirstRep()).thenReturn(episodeOfCareRef);
        when(episodeOfCareRef.getResource()).thenReturn(episodeOfCareRefResource);
        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(ce.isSetCode()).thenReturn(true);
        when(ii.isSetRoot()).thenReturn(true);
        when(encounter.hasIncomingReferral()).thenReturn(true);
        when(encounter.getIncomingReferralFirstRep()).thenReturn(reference);
        when(reference.getResource()).thenReturn(referralRequest);
        when(reference.getResource().fhirType()).thenReturn("ReferralRequest");
    }

    @Test
    public void mapComposition() {
        Composition composition = compositionMapper.mapComposition(clinicalDocument, encounter, carePlans);

        assertThat(composition.getTitle()).isEqualTo("111 Report");
        Coding code = composition.getType().getCodingFirstRep();
        assertThat(code.getCode()).isEqualTo("371531000");
        assertThat(code.getSystem()).isEqualTo("http://snomed.info/sct");
        assertThat(code.getDisplay()).isEqualTo("Report of clinical encounter (record artifact)");
        assertThat(composition.getStatus()).isEqualTo(Composition.CompositionStatus.FINAL);
        assertThat(composition.getConfidentiality()).isEqualTo(Composition.DocumentConfidentiality.V);
        assertThat(composition.getRelatesTo().get(0).getCode()).isEqualTo(Composition.DocumentRelationshipType.REPLACES);
        assertThat(composition.getSection().get(0).getTitle()).isEqualTo("CarePlan");
        assertThat(composition.getSection().get(1).getTitle()).isEqualTo("ReferralRequest");
    }
}
