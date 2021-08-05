package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Observation.ObservationStatus.FINAL;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtilTest.verifyUUID;

import java.util.List;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.iucds.cda.ucr.ST;
import uk.nhs.connect.iucds.cda.ucr.StrucDocContent;
import uk.nhs.connect.iucds.cda.ucr.StrucDocText;

@ExtendWith(MockitoExtension.class)
public class ObservationMapperTest {

    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";
    private static final String PRESENTING_COMPLAINT_DISPLAY = "Presenting complaint";
    private static final String PRESENTING_COMPLAINT_CODE = "33962009";
    private static final String PATIENTS_CONDITION_REGEXP = "Patient.s Reported Condition";
    private static final String OBSERVATION_VALUE = "Patient has an insect bite.";

    @InjectMocks
    private ObservationMapper observationMapper;

    @Mock
    private NodeUtil nodeUtil;

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Mock
    private Encounter encounter;

    @Mock
    private Reference subject;

    @BeforeEach
    public void setUp() {
        POCDMT000002UK01Component2 component2 = mock(POCDMT000002UK01Component2.class);
        when(component2.isSetStructuredBody()).thenReturn(true);
        POCDMT000002UK01StructuredBody structuredBody = mock(POCDMT000002UK01StructuredBody.class);
        POCDMT000002UK01Component3 component3 = mock(POCDMT000002UK01Component3.class);
        POCDMT000002UK01Section section = mock(POCDMT000002UK01Section.class);
        POCDMT000002UK01Component5 component5 = mock(POCDMT000002UK01Component5.class);
        POCDMT000002UK01Section innerSection = mock(POCDMT000002UK01Section.class);
        StrucDocText text = mock(StrucDocText.class);
        StrucDocContent contentItem = mock(StrucDocContent.class);
        StrucDocContent[] content = new StrucDocContent[] {contentItem};
        when(text.getContentArray()).thenReturn(content);
        when(innerSection.getText()).thenReturn(text);
        ST title = mock(ST.class);
        when(nodeUtil.getNodeValueString(title)).thenReturn("Patient's Reported Condition");
        when(nodeUtil.getNodeValueString(contentItem)).thenReturn(OBSERVATION_VALUE);
        when(innerSection.getTitle()).thenReturn(title);
        when(component5.getSection()).thenReturn(innerSection);
        POCDMT000002UK01Component5[] components5 = new POCDMT000002UK01Component5[] {component5};
        when(section.getComponentArray()).thenReturn(components5);
        when(component3.getSection()).thenReturn(section);
        POCDMT000002UK01Component3[] components3 = new POCDMT000002UK01Component3[] {component3};
        when(structuredBody.getComponentArray()).thenReturn(components3);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(encounter.getSubject()).thenReturn(subject);
    }

    @Test
    public void shouldMapObservation() {
        List<Observation> observations = observationMapper.mapObservations(clinicalDocument, encounter);

        assertThat(observations.size()).isEqualTo(1);
        Observation observation = observations.get(0);
        assertThat(observation.getStatus()).isEqualTo(FINAL);
        assertThat(verifyUUID(observation.getIdElement().getValue())).isEqualTo(true);
        assertThat(observation.getValueStringType().toString()).isEqualTo(OBSERVATION_VALUE);
        Coding codingFirstRep = observation.getCode().getCodingFirstRep();
        assertThat(codingFirstRep.getCode()).isEqualTo(PRESENTING_COMPLAINT_CODE);
        assertThat(codingFirstRep.getDisplay()).isEqualTo(PRESENTING_COMPLAINT_DISPLAY);
        assertThat(codingFirstRep.getSystem()).isEqualTo(SNOMED_SYSTEM);
        assertThat(observation.getContext().getResource()).isEqualTo(encounter);
        assertThat(observation.getSubject()).isEqualTo(subject);
    }
}
