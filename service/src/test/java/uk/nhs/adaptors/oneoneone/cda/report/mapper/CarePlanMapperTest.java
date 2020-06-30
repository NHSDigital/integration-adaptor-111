package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component3;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarePlanMapperTest {

    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;

    @Mock
    private Encounter encounter;

    @InjectMocks
    private CarePlanMapper carePlanMapper;

    @Before
    public void setup() {
        POCDMT000002UK01Component2 component2 = mock(POCDMT000002UK01Component2.class);
        POCDMT000002UK01Component3 component3 = mock(POCDMT000002UK01Component3.class);
        POCDMT000002UK01Section section = mock(POCDMT000002UK01Section.class);
        POCDMT000002UK01StructuredBody structuredBody = mock(POCDMT000002UK01StructuredBody.class);

        when(clinicalDocument.getComponent()).thenReturn(component2);
        when(component2.getStructuredBody()).thenReturn(structuredBody);
        when(component3.getSection()).thenReturn(section);
        when(structuredBody.getComponentArray()).thenReturn(new POCDMT000002UK01Component3[]{component3});
    }

    @Test
    public void shouldMapITKReportToCarePlan() {
        encounter.setSubject(new Reference().setDisplay("SubjectReference"));

        List<CarePlan> carePlans = carePlanMapper.mapCarePlan(clinicalDocument, encounter);
        if (carePlans.isEmpty()) return;

        CarePlan carePlan = carePlans.get(0);
        assertThat(carePlan.getStatus(), is(CarePlan.CarePlanStatus.COMPLETED));
        assertThat(carePlan.getIntent(), is(CarePlan.CarePlanIntent.PLAN));
        assertThat(carePlan.getTitle(), is("Some advice"));
        assertThat(carePlan.getDescription(), is("Stay indoors"));
        assertThat(carePlan.getText(), is("Stay indoors"));
        assertThat(carePlan.getLanguage(), is("Stay indoors"));
        assertThat(carePlan.getPeriod(), is("Stay indoors"));
        assertThat(carePlan.getContext().getResource(), sameInstance(encounter));
    }
}