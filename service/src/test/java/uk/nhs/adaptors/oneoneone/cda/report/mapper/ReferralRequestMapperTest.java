package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.connect.iucds.cda.ucr.ClinicalDocumentDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ReferralRequestMapperTest {

    @Mock
    private HealthcareServiceMapper healthcareServiceMapper;
    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private ReferralRequestMapper referralRequestMapper;

    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    private Encounter encounter;

    private Reference patientRef = new Reference();
    private Reference deviceRef = new Reference("Device/1");
    private HealthcareService healthcareService = new HealthcareService();
    private Reference serviceProviderRef = new Reference("HealthcareService/1");
    private Reference encounterRef;

    @Before
    public void setup() throws IOException, XmlException {
        URL resource = getClass().getResource("/xml/example-clinical-doc.xml");

        clinicalDocument = ClinicalDocumentDocument1.Factory.parse(resource).getClinicalDocument();

        encounter = new Encounter();
        encounter
                .setServiceProvider(serviceProviderRef)
                .setSubject(patientRef)
                .setId("Encounter/1");

        encounterRef = new Reference(encounter.getServiceProvider().toString());

        Mockito.when(healthcareServiceMapper
                .mapHealthcareService(any()))
                .thenReturn(healthcareService);

        Patient patient = new Patient();
        Mockito.when(patientMapper.mapPatient(any())).thenReturn(patient);

        referralRequestMapper = new ReferralRequestMapper(healthcareServiceMapper, patientMapper);
    }

    @Test
    public void transform() {

        ReferralRequest referralRequest = referralRequestMapper
                .mapReferralRequest(clinicalDocument, encounter);

        assertEquals("Status", ReferralRequest.ReferralRequestStatus.ACTIVE, referralRequest.getStatus());
        assertEquals("Intent", ReferralRequest.ReferralCategory.PLAN, referralRequest.getIntent());
        assertEquals("Priority", ReferralRequest.ReferralPriority.ROUTINE, referralRequest.getPriority());

        assertTrue("occurrence", referralRequest.hasOccurrence());
        assertTrue("authoredOn", referralRequest.hasAuthoredOn());

        assertTrue("requester.agent",
                deviceRef.equalsDeep(referralRequest.getRequester().getAgent()));
        assertTrue("requester.onBehalfOf",
                encounterRef.equalsDeep(referralRequest.getRequester().getOnBehalfOf()));
        assertTrue("context",
                new Reference(encounter).equalsDeep(referralRequest.getContext()));
        assertTrue("subject",
                patientRef.equalsDeep(referralRequest.getSubject()));
    }
}