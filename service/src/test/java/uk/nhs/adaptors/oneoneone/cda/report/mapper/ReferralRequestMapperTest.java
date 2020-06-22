package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.*;
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
    @Mock
    private EncounterMapper encounterMapper;

    @InjectMocks
    private ReferralRequestMapper referralRequestMapper;

    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    private Encounter encounter;

    private Reference patientRef = new Reference("Patient/1");
    private Reference deviceRef = new Reference("HealthcareService/1");
    private Reference conditionRef = new Reference("Condition/1");
    private HealthcareService healthcareService = new HealthcareService();
    private Reference serviceProviderRef = new Reference("HealthcareService/1");

    @Before
    public void setup() throws IOException, XmlException {
        URL resource = getClass().getResource("/xml/example-clinical-doc.xml");

        clinicalDocument = ClinicalDocumentDocument1.Factory.parse(resource).getClinicalDocument();

        encounter = new Encounter();
        encounter
                .setServiceProvider(serviceProviderRef)
                .setSubject(patientRef)
                .setId("Encounter/1");

        Mockito.when(encounterMapper.mapEncounter(any())).thenReturn(encounter);
        Mockito.when(healthcareServiceMapper
                .transformRecipient(any()))
                .thenReturn(healthcareService);

        Patient patient = new Patient();
        Mockito.when(patientMapper.transform(any())).thenReturn(patient);

        referralRequestMapper = new ReferralRequestMapper(healthcareServiceMapper, patientMapper, encounterMapper);
    }

    @Test
    public void transform() {

        ReferralRequest referralRequest = referralRequestMapper
                .transform(clinicalDocument);

        assertEquals("Status", ReferralRequest.ReferralRequestStatus.ACTIVE, referralRequest.getStatus());
        assertEquals("Intent", ReferralRequest.ReferralCategory.PLAN, referralRequest.getIntent());
        assertEquals("Priority", ReferralRequest.ReferralPriority.ROUTINE, referralRequest.getPriority());

        assertTrue("occurrence", referralRequest.hasOccurrence());
        assertTrue("authoredOn", referralRequest.hasAuthoredOn());

        assertTrue("requester.agent",
                deviceRef.equalsDeep(referralRequest.getRequester().getAgent()));
        assertTrue("requester.onBehalfOf",
                encounter.getServiceProvider().equalsDeep(referralRequest.getRequester().getOnBehalfOf()));
        assertTrue("context",
                new Reference(encounter).equalsDeep(referralRequest.getContext()));
        assertTrue("subject",
                encounter.getSubject().equalsDeep(referralRequest.getSubject()));
    }
}