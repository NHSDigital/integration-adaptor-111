package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.CS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;
import uk.nhs.connect.iucds.cda.ucr.ST;
import uk.nhs.connect.iucds.cda.ucr.StrucDocContent;
import uk.nhs.connect.iucds.cda.ucr.StrucDocText;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarePlanMapperTest {
    public static final String URN_UUID = "urn:uuid:";
    private static final String LANG = "EN";
    private static final String SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    private static final String INFORMATION_ADVICE_GIVEN = "1052951000000105";
    private static final String DESCRIPTION = "description";
    private static final String DIV_AS_STRING = String.format("<div xmlns=\"http://www.w3.org/1999/xhtml\">%s</div>", DESCRIPTION);
    private static final String TITLE = "title";

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Mock
    private Encounter encounter;

    @Mock
    private Period period;

    @Mock
    private POCDMT000002UK01Section section;

    @Mock
    private POCDMT000002UK01Component5 component5;

    @Mock
    private ST title;

    @Mock
    private CE code;

    @Mock
    private CS cs;

    @Mock
    private StrucDocText strucDocText;

    @InjectMocks
    private CarePlanMapper carePlanMapper;

    @Before
    public void setup() {
        POCDMT000002UK01Component2 component2 = mock(POCDMT000002UK01Component2.class);
        POCDMT000002UK01Component3 component3 = mock(POCDMT000002UK01Component3.class);
        POCDMT000002UK01StructuredBody structuredBody = mock(POCDMT000002UK01StructuredBody.class);

        code = CE.Factory.newInstance();
        code.setCodeSystem(SNOMED);
        code.setCode(INFORMATION_ADVICE_GIVEN);

        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        when(component3.getSection()).thenReturn(section);
        when(component5.getSection()).thenReturn(section);
        when(structuredBody.getComponentArray()).thenReturn(new POCDMT000002UK01Component3[]{component3});

        encounter.setPeriod(period);
        title.setLanguage(LANG);
        cs.setCode(LANG);
        mockLanguageXmlStructure(cs);

        Patient subject = mock(Patient.class);
        Reference subjectReference = new Reference(subject);
        when(encounter.getSubject()).thenReturn(subjectReference);
        when(encounter.getSubjectTarget()).thenReturn(subject);
        when(encounter.getPeriod()).thenReturn(period);
    }

    @Test
    public void shouldMapITKReportToCarePlan() {
        mockSection();

        List<CarePlan> carePlans = carePlanMapper.mapCarePlan(clinicalDocument, encounter);
        assertThat(carePlans).isNotEmpty();

        CarePlan carePlan = carePlans.get(0);
        assertThat(carePlan.getIdElement().getValue()).startsWith(URN_UUID);
        assertThat(carePlan.getStatus()).isEqualTo(CarePlan.CarePlanStatus.ACTIVE);

        assertThat(carePlan.getIntent()).isEqualTo(CarePlan.CarePlanIntent.PLAN);
        assertThat(carePlan.getContext().getResource()).isEqualTo(encounter);
        assertThat(carePlan.getContextTarget()).isEqualTo(encounter);
        assertThat(carePlan.getPeriod()).isEqualTo(period);
        assertThat(carePlan.getSubject()).isEqualTo(encounter.getSubject());
        assertThat(carePlan.getSubjectTarget()).isEqualTo(encounter.getSubjectTarget());
        assertThat(carePlan.getLanguage()).isEqualTo(LANG);

        assertThat(carePlan.getText().getDivAsString()).isEqualTo(DIV_AS_STRING);
        assertThat(carePlan.getTitle()).isEqualTo(TITLE);
        assertThat(carePlan.getDescription()).isEqualTo(DESCRIPTION);
    }

    private void mockSection() {
        when(section.sizeOfComponentArray()).thenReturn(2).thenReturn(0);
        when(section.getComponentArray()).thenReturn(new POCDMT000002UK01Component5[]{component5, component5});
        when(section.getCode()).thenReturn(code);
        when(section.isSetLanguageCode()).thenReturn(true);
        when(section.isSetTitle()).thenReturn(true);
        when(section.isSetText()).thenReturn(true);
        when(section.getText()).thenReturn(strucDocText);
        when(section.getText().sizeOfContentArray()).thenReturn(1);

        when(section.getTitle()).thenReturn(title);
        mockTitleXmlStructure(title);

        when(section.getText()).thenReturn(strucDocText);
        mockDescriptionXmlStructure(strucDocText);

        when(section.getLanguageCode()).thenReturn(cs);
    }

    private void mockDescriptionXmlStructure(StrucDocText description) {
        StrucDocContent structuredContent = mock(StrucDocContent.class);
        when(description.getContentArray(ArgumentMatchers.anyInt())).thenReturn(structuredContent);

        Node structuredContentNode = mock(Node.class);
        when(structuredContent.getDomNode()).thenReturn(structuredContentNode);
        when(structuredContentNode.getFirstChild()).thenReturn(structuredContentNode);
        when(structuredContentNode.getNodeValue()).thenReturn(DESCRIPTION);
    }

    private void mockTitleXmlStructure(ST title) {
        Node node = mock(Node.class);
        when(title.getDomNode()).thenReturn(node);
        mockNodeValue(node, TITLE);
    }

    private void mockLanguageXmlStructure(CS cs) {
        Node node = mock(Node.class);
        when(cs.getDomNode()).thenReturn(node);
        mockNodeValue(node, LANG);
    }

    private void mockNodeValue(Node node, String retValue) {
        when(node.getFirstChild()).thenReturn(node);
        when(node.getNodeValue()).thenReturn(retValue);
    }
}