package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.assertj.core.api.Assertions;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

import java.util.List;
import java.util.Optional;

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
    private POCDMT000002UK01Section carePlanSection;

    @Mock
    EncounterMapper encounterMapper;

    @InjectMocks
    private CarePlanMapper carePlanMapper;

    @Test
    public void shouldMapITKReportToCarePlan() {
        POCDMT000002UK01Component1 component = mock(POCDMT000002UK01Component1.class);
        when(clinicalDocument.getComponentOf()).thenReturn(component);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument);

        List<Reference> carePlanRefs = carePlanMapper.mapCarePlan(clinicalDocument, encounter);
        Reference carePlanRef = carePlanRefs.get(0);
        CarePlan carePlan = new CarePlan(); // XXX:

        assertThat(carePlan.getStatus(), is(CarePlan.CarePlanStatus.COMPLETED));
        assertThat(carePlan.getIntent(), is(CarePlan.CarePlanIntent.PLAN));
        assertThat(carePlan.getTitle(), is("Some advice"));
        assertThat(carePlan.getDescription(), is("Stay indoors"));
        assertThat(carePlan.getText(), is("Stay indoors"));
        assertThat(carePlan.getLanguage(), is("Stay indoors"));
        assertThat(carePlan.getPeriod(), is("Stay indoors"));
        assertThat(carePlan.getContext().getResource(), sameInstance(encounter));
    }

    @Test
    public void shouldMapCarePlanNoActivity() {
    }
}