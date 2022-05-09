package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Composition.CompositionStatus.FINAL;
import static org.hl7.fhir.dstu3.model.Composition.DocumentRelationshipType.REPLACES;
import static org.hl7.fhir.dstu3.model.Narrative.NarrativeStatus.GENERATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.TestResourceUtils.readResourceAsString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.PractitionerRole;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Node;

import uk.nhs.adaptors.oneoneone.cda.report.controller.utils.XmlUtils;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01RelatedDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.iucds.cda.ucr.ST;
import uk.nhs.connect.iucds.cda.ucr.TS;

@ExtendWith(MockitoExtension.class)
public class CompositionMapperTest {

    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";
    private static final String ITK_SECTION_TEXT = readResourceAsString("/xml/itkSectionText.xml");
    private static final String COMPOSITION_SECTION_DIV = readResourceAsString("/xml/compositionSectionDiv.xml");
    private static final String NESTED_SECTION_TITLE = "THE TITLE";
    private static final String EFFECTIVE_DATE = "20220505";
    private final CarePlan carePlan = new CarePlan();
    private final List<CarePlan> carePlans = Collections.singletonList(carePlan);
    private final TS effectiveTime = TS.Factory.newInstance();

    @InjectMocks
    private CompositionMapper compositionMapper;
    @Mock
    private NodeUtil nodeUtil;
    @Mock
    private XmlUtils xmlUtils;
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
    private ReferralRequest referralRequest;
    @Mock
    private ResourceUtil resourceUtil;
    @Mock
    private List<PractitionerRole> practitionerRoles;
    @Mock
    private POCDMT000002UK01Component2 component2;
    @Mock
    private QuestionnaireResponse questionnaireResponse;
    private List<QuestionnaireResponse> questionnaireResponseList;

    @BeforeEach
    public void setUp() {
        effectiveTime.setValue(EFFECTIVE_DATE);
        questionnaireResponseList = new ArrayList<>();
        questionnaireResponseList.add(questionnaireResponse);
        POCDMT000002UK01RelatedDocument1[] relatedDocsArray = {mock(POCDMT000002UK01RelatedDocument1.class)};
        when(clinicalDocument.getRelatedDocumentArray()).thenReturn(relatedDocsArray);
        when(clinicalDocument.getRelatedDocumentArray(0)).thenReturn(relatedDocument1);
        when(relatedDocument1.getParentDocument()).thenReturn(parentDocument1);
        when(parentDocument1.getIdArray(0)).thenReturn(ii);
        when(clinicalDocument.getSetId()).thenReturn(ii);
        when(clinicalDocument.getEffectiveTime()).thenReturn(effectiveTime);
        when(ii.getRoot()).thenReturn("411910CF-1A76-4330-98FE-C345DDEE5553");
        when(clinicalDocument.getConfidentialityCode()).thenReturn(ce);
        when(ce.getCode()).thenReturn("V");
        when(ce.isSetCode()).thenReturn(true);
        when(ii.isSetRoot()).thenReturn(true);
        when(referralRequest.fhirType()).thenReturn("ReferralRequest");
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
        when(resourceUtil.createReference(encounter)).thenReturn(new Reference(encounter));
        when(resourceUtil.createReference(questionnaireResponse)).thenReturn(new Reference(questionnaireResponse));
        when(resourceUtil.createReference(carePlan)).thenReturn(new Reference(carePlan));
        when(resourceUtil.createReference(referralRequest)).thenReturn(new Reference(referralRequest));
        mockStructuredBody();
    }

    private void mockStructuredBody() {
        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(component2.isSetStructuredBody()).thenReturn(true);
        POCDMT000002UK01StructuredBody structuredBody = mock(POCDMT000002UK01StructuredBody.class);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        POCDMT000002UK01Component3 component3 = mock(POCDMT000002UK01Component3.class);
        when(structuredBody.getComponentArray()).thenReturn(new POCDMT000002UK01Component3[] {component3});
        POCDMT000002UK01Section section = mock(POCDMT000002UK01Section.class);
        when(component3.getSection()).thenReturn(section);
        POCDMT000002UK01Component5 component5 = mock(POCDMT000002UK01Component5.class);
        when(section.getComponentArray()).thenReturn(new POCDMT000002UK01Component5[] {component5});
        POCDMT000002UK01Section innerSection = mock(POCDMT000002UK01Section.class);
        when(component5.getSection()).thenReturn(innerSection);
        when(innerSection.isSetTitle()).thenReturn(true);
        ST title = mock(ST.class);
        when(innerSection.getTitle()).thenReturn(title);
        Node titleNode = mock(Node.class);
        when(title.getDomNode()).thenReturn(titleNode);
        when(nodeUtil.getAllText(titleNode)).thenReturn(NESTED_SECTION_TITLE);
        Node innerSectionNode = mock(Node.class);
        when(innerSection.getDomNode()).thenReturn(innerSectionNode);
        when(xmlUtils.serialize(innerSectionNode)).thenReturn(ITK_SECTION_TEXT);
        Node textNode = mock(Node.class);
        when(xmlUtils.getSingleNode(any(Node.class), eq("//text"))).thenReturn(textNode);
        when(xmlUtils.serialize(textNode)).thenReturn(ITK_SECTION_TEXT);
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldMapComposition() {
        Composition composition = compositionMapper.mapComposition(clinicalDocument, encounter, carePlans, questionnaireResponseList,
            referralRequest, practitionerRoles);

        Coding code = composition.getType().getCodingFirstRep();
        String questionnaireResponseTitle = "QuestionnaireResponse";

        assertThat(composition.getTitle()).isEqualTo("111 Report");
        assertThat(code.getCode()).isEqualTo("371531000");
        assertThat(code.getSystem()).isEqualTo("http://snomed.info/sct");
        assertThat(code.getDisplay()).isEqualTo("Report of clinical encounter (record artifact)");
        assertThat(composition.getStatus()).isEqualTo(FINAL);
        assertThat(composition.getConfidentiality()).isEqualTo(Composition.DocumentConfidentiality.V);
        assertThat(composition.getRelatesTo().get(0).getCode()).isEqualTo(REPLACES);
        Composition.SectionComponent sectionComponent = composition.getSection().get(0);
        assertThat(sectionComponent.getSection().size()).isEqualTo(1);
        assertThat(sectionComponent.getSection().get(0).getTitle()).isEqualTo(NESTED_SECTION_TITLE);
        assertThat(sectionComponent.getSection().get(0).getText().getStatus()).isEqualTo(GENERATED);
        assertThat(sectionComponent.getSection().get(0).getText().getDivAsString()).isEqualTo(COMPOSITION_SECTION_DIV);
        assertThat(composition.getSection().get(1).getTitle()).isEqualTo("CarePlan");
        assertThat(composition.getSection().get(2).getTitle()).isEqualTo("ReferralRequest");
        assertThat(composition.getSection().get(3).getEntry().get(0).getResource()).isEqualTo(questionnaireResponse);
        assertThat(composition.getSection().get(3).getTitle()).isEqualTo(questionnaireResponseTitle);
        assertThat(composition.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(composition.getDateElement().getValue()).isEqualTo(DateUtil.parse(effectiveTime.getValue()).getValue());
    }
}
