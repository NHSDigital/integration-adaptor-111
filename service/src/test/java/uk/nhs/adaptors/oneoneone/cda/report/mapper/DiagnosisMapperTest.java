package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.DiagnosisComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.CS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

@RunWith(MockitoJUnitRunner.class)
public class DiagnosisMapperTest {

    private final String role = "role";
    @InjectMocks
    private DiagnosisMapper diagnosisMapper;
    @Mock
    private ConditionMapper conditionMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    @Mock
    private Condition condition;
    @Mock
    private POCDMT000002UK01Component2 component2;
    @Mock
    private POCDMT000002UK01StructuredBody structuredBody;
    @Mock
    private POCDMT000002UK01Section section01;
    @Mock
    private POCDMT000002UK01Component3 component3;
    @Mock
    private POCDMT000002UK01Entry entry;
    @Mock
    private POCDMT000002UK01Encounter itkencounter;
    @Mock
    private CE priorityCode;
    @Mock
    private CS statusCode;
    @Mock
    private Encounter encounter;
    private Reference conditionRef;

    @Before
    public void setUp() {
        POCDMT000002UK01Component3[] componentArray = new POCDMT000002UK01Component3[1];
        componentArray[0] = component3;
        POCDMT000002UK01Entry[] entryArray = new POCDMT000002UK01Entry[1];
        entryArray[0] = entry;
        conditionRef = new Reference(condition);
        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(component2.isSetStructuredBody()).thenReturn(true);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        when(structuredBody.getComponentArray()).thenReturn(componentArray);
        when(component3.getSection()).thenReturn(section01);
        when(section01.getEntryArray()).thenReturn(entryArray);
        when(entry.getEncounter()).thenReturn(itkencounter);
        when(itkencounter.isSetText()).thenReturn(true);
        when(itkencounter.isSetStatusCode()).thenReturn(true);
        when(itkencounter.isSetCode()).thenReturn(true);
        when(conditionMapper.mapCondition(any(), any())).thenReturn(condition);
        when(itkencounter.getStatusCode()).thenReturn(statusCode);
        when(statusCode.getCode()).thenReturn(role);
        when(itkencounter.getPriorityCode()).thenReturn(priorityCode);
        String priority = "1";
        when(priorityCode.getCode()).thenReturn(priority);
        when(entry.isSetEncounter()).thenReturn(true);
    }

    @Test
    public void mapComposition() {
        DiagnosisComponent diagnosisComponent = diagnosisMapper.mapDiagnosis(clinicalDocument, encounter);

        assertThat(diagnosisComponent.getRole().getText()).isEqualTo(role);
        assertThat(diagnosisComponent.getCondition().getReference()).isEqualTo(conditionRef.getReference());
        assertThat(diagnosisComponent.getConditionTarget()).isEqualTo(condition);
        int priorityInt = 1;
        assertThat(diagnosisComponent.getRank()).isEqualTo(priorityInt);
    }
}
