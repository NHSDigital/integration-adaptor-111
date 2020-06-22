package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.*;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class PatientMapperTest {


    @Mock
    private ContactPointMapper contactPointMapper;

    @InjectMocks
    private PatientMapper patientMapper;

    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    private Date dob;

    @Before
    public void setup() throws IOException, XmlException, ParseException {
        ContactPoint contactPoint = new ContactPoint();
        Mockito.when(contactPointMapper.mapContactPoint(any())).thenReturn(contactPoint);

        URL resource = getClass().getResource("/xml/example-clinical-doc.xml");
        clinicalDocument = ClinicalDocumentDocument1.Factory.parse(resource).getClinicalDocument();

        dob = new SimpleDateFormat("yyyy/MM/dd").parse("1989/01/01");
    }

    @Test
    public void patientTest() {
        POCDMT000002UK01PatientRole patient = clinicalDocument.getRecordTargetArray(0).getPatientRole();
        Patient fhirPatient = patientMapper.transform(patient);

        assertThat(fhirPatient.getBirthDate(), equalTo(dob));
        assertThat(fhirPatient.getLanguage(), equalTo("en"));
        assertThat(fhirPatient.getGender().toCode(), is("male"));
    }

}
