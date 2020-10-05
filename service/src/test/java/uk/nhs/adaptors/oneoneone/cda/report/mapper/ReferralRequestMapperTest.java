package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@ExtendWith(MockitoExtension.class)
public class ReferralRequestMapperTest {

    private final Reference patientRef = new Reference();
    private final Reference deviceRef = new Reference("Device/1");
    private final Reference serviceProviderRef = new Reference("HealthcareService/1");
    @InjectMocks
    private ReferralRequestMapper referralRequestMapper;
    @Mock
    private HealthcareService healthcareService;
    @Mock
    private List<HealthcareService> healthcareServiceList;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument;
    private Encounter encounter;
    @Mock
    private Reference condition;
    @Mock
    private ProcedureRequestMapper procedureRequestMapper;
    @Mock
    private ProcedureRequest procedureRequest;

    @BeforeEach
    public void setup() {
        healthcareServiceList = new ArrayList<>();
        healthcareServiceList.add(healthcareService);

        encounter = new Encounter();
        String encounterId = "Encounter/1";
        encounter
            .setServiceProvider(serviceProviderRef)
            .setSubject(patientRef)
            .setId(encounterId);
    }

    @Test
    public void shouldMapReferralRequest() {
        when(procedureRequestMapper.mapProcedureRequest(any(), any())).thenReturn(procedureRequest);

        ReferralRequest referralRequest = referralRequestMapper
            .mapReferralRequest(clinicalDocument, encounter, healthcareServiceList, condition);

        assertThat(ReferralRequest.ReferralRequestStatus.ACTIVE).isEqualTo(referralRequest.getStatus());
        assertThat(ReferralRequest.ReferralCategory.PLAN).isEqualTo(referralRequest.getIntent());
        assertThat(ReferralRequest.ReferralPriority.ROUTINE).isEqualTo(referralRequest.getPriority());
        assertThat(referralRequest.hasOccurrence()).isEqualTo(true);
        assertThat(referralRequest.hasAuthoredOn()).isEqualTo(true);
        assertThat(deviceRef.getReference()).isEqualTo(referralRequest.getRequester().getAgent().getReference());
        assertThat(serviceProviderRef.getReference()).isEqualTo(referralRequest.getRequester().getOnBehalfOf().getReference());
        assertThat(new Reference(encounter).getReference()).isEqualTo(referralRequest.getContext().getReference());
        assertThat(patientRef.getReference()).isEqualTo(referralRequest.getSubject().getReference());
        assertThat(referralRequest.getSupportingInfo().get(0).getResource()).isEqualTo(procedureRequest);
    }
}