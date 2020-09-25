package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.ACTIVE;
import static org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus.UNKNOWN;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Node;

import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.ED;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.CS;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Condition.ConditionClinicalStatus.ACTIVE;
import static org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus.UNKNOWN;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConditionMapperTest {

    private static final String EFFECTIVE_TIME_STRING = "201706011400+00";
    private static final String CONIDITION_TEXT = "Condition text";
    private static final String LANGUAGE_CODE = "en";
    private static final String LANGUAGE_SYSTEM = "http://hl7.org/fhir/ValueSet/languages";
    private static final String LANGUAGE_DISPLAY = "English";
    private final Reference individualRef = new Reference("IndividualRef/1");
    @InjectMocks
    private ConditionMapper conditionMapper;

    @Mock
    private POCDMT000002UK01Section itkSection;
    @Mock
    private POCDMT000002UK01Component5 component;
    @Mock
    private CS cs;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private POCDMT000002UK01Component2 component2;
    @Mock
    private POCDMT000002UK01StructuredBody structuredBody;
    @Mock
    private POCDMT000002UK01Component3 component3;
    @Mock
    private POCDMT000002UK01Section section;
    @Mock
    private POCDMT000002UK01Entry entry;
    @Mock
    private POCDMT000002UK01Encounter itkEncounter;
    @Mock
    private ED ed;
    @Mock
    private Node node;
    @Mock
    private Encounter encounter;
    @Mock
    private IVLTS time;
    @Mock
    private Encounter.EncounterParticipantComponent participantFirstRep;
    @Mock
    private NodeUtil nodeUtil;

    @BeforeEach
    public void setUp() {
        when(itkSection.getComponentArray()).thenReturn(new POCDMT000002UK01Component5[] {component});
        when(component.getSection()).thenReturn(itkSection);
        when(itkSection.isSetLanguageCode()).thenReturn(true);
        when(itkSection.getLanguageCode()).thenReturn(cs);
        when(cs.isSetCode()).thenReturn(true);
        when(cs.getCode()).thenReturn(LANGUAGE_CODE);
        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(component2.isSetStructuredBody()).thenReturn(true);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        POCDMT000002UK01Component3[] component3s = new POCDMT000002UK01Component3[] {component3};
        when(structuredBody.getComponentArray()).thenReturn(component3s);
        when(component3.getSection()).thenReturn(section);
        POCDMT000002UK01Entry[] entries = new POCDMT000002UK01Entry[] {entry};
        when(section.getEntryArray()).thenReturn(entries);
        when(entry.isSetEncounter()).thenReturn(true);
        when(entry.getEncounter()).thenReturn(itkEncounter);
        when(itkEncounter.isSetEffectiveTime()).thenReturn(true);
        when(itkEncounter.getEffectiveTime()).thenReturn(time);
        when(time.getValue()).thenReturn(EFFECTIVE_TIME_STRING);
        when(itkEncounter.isSetText()).thenReturn(true);
        when(itkEncounter.getText()).thenReturn(ed);
        when(ed.getDomNode()).thenReturn(node);
        when(nodeUtil.getAllText(node)).thenReturn(CONIDITION_TEXT);
        when(encounter.getParticipantFirstRep()).thenReturn(participantFirstRep);
        when(participantFirstRep.getIndividual()).thenReturn(individualRef);
    }

    @Test
    public void mapCondition() {
        Condition condition = conditionMapper.mapCondition(clinicalDocument, encounter);
        Condition condition = conditionMapper.mapCondition(itkSection, itkEncounter, encounter);


        assertThat(condition.getClinicalStatus()).isEqualTo(ACTIVE);
        assertThat(condition.getVerificationStatus()).isEqualTo(UNKNOWN);
        assertThat(condition.getAssertedDate()).isEqualTo(DateUtil.parse(EFFECTIVE_TIME_STRING));
        assertThat(condition.getCategoryFirstRep().getText()).isEqualTo(CONIDITION_TEXT);
        assertThat(condition.getCode().getCoding().get(0).getCode()).isEqualTo(LANGUAGE_CODE);
        assertThat(condition.getCode().getCoding().get(0).getSystem()).isEqualTo(LANGUAGE_SYSTEM);
        assertThat(condition.getCode().getCoding().get(0).getDisplay()).isEqualTo(LANGUAGE_DISPLAY);
    }
}
