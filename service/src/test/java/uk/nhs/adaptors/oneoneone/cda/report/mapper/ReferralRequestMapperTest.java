package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssociatedEntity;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Author;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant1;
import uk.nhs.connect.iucds.cda.ucr.TS;

@ExtendWith(MockitoExtension.class)
public class ReferralRequestMapperTest {

    private static final String PARTICIPANT_TYPE_CODE_REFT = "REFT";
    private static final String PARTICIPANT_TYPE_CODE_CALLBCK = "CALLBCK";
    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";
    private static final String AUTHOR_TIME = "20210304";

    private final Reference patientRef = new Reference();
    private final Reference deviceRef = new Reference("Device/1");
    private final Reference serviceProviderRef = new Reference("HealthcareService/1");
    private final Period occurrence = new Period().setStart(new Date());

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
    @Mock
    private ResourceUtil resourceUtil;
    @Mock
    private POCDMT000002UK01Author author;
    @Mock
    private POCDMT000002UK01Participant1 participantREFT;
    @Mock
    private POCDMT000002UK01Participant1 participantCALLBCK;
    @Mock
    private PractitionerMapper practitionerMapper;
    @Mock
    private Practitioner practitioner;
    @Mock
    private POCDMT000002UK01AssociatedEntity associatedEntity;
    @Mock
    private PeriodMapper periodMapper;

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

        TS ts = TS.Factory.newInstance();
        ts.setValue(AUTHOR_TIME);
        POCDMT000002UK01Author[] authorArray = new POCDMT000002UK01Author[1];
        authorArray[0] = author;

        TS effectiveTime = TS.Factory.newInstance();
        effectiveTime.setValue("20220304");

        when(procedureRequestMapper.mapProcedureRequest(any(), any(), any())).thenReturn(procedureRequest);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
        when(resourceUtil.createReference(encounter)).thenReturn(new Reference(encounter));
        when(resourceUtil.createReference(procedureRequest)).thenReturn(new Reference(procedureRequest));
        when(resourceUtil.createReference(practitioner)).thenReturn(new Reference(practitioner));
        when(author.getTime()).thenReturn(ts);
        when(author.getTypeCode()).thenReturn("AUT");
        when(clinicalDocument.getAuthorArray()).thenReturn(authorArray);
        when(clinicalDocument.getParticipantArray()).thenReturn(new POCDMT000002UK01Participant1[] {participantREFT, participantCALLBCK});
        when(clinicalDocument.getEffectiveTime()).thenReturn(effectiveTime);
        when(participantREFT.getTypeCode()).thenReturn(PARTICIPANT_TYPE_CODE_REFT);
        when(participantCALLBCK.getTypeCode()).thenReturn(PARTICIPANT_TYPE_CODE_CALLBCK);
        when(participantREFT.getAssociatedEntity()).thenReturn(associatedEntity);
        when(practitionerMapper.mapPractitioner(associatedEntity)).thenReturn(practitioner);
        when(periodMapper.mapPeriod(effectiveTime)).thenReturn(occurrence);
    }

    @Test
    public void shouldMapReferralRequest() {
        ReferralRequest referralRequest = referralRequestMapper
            .mapReferralRequest(clinicalDocument, encounter, healthcareServiceList, condition, deviceRef);

        assertThat(ReferralRequest.ReferralRequestStatus.ACTIVE).isEqualTo(referralRequest.getStatus());
        assertThat(ReferralRequest.ReferralCategory.PLAN).isEqualTo(referralRequest.getIntent());
        assertThat(ReferralRequest.ReferralPriority.ROUTINE).isEqualTo(referralRequest.getPriority());
        assertThat(referralRequest.getOccurrence()).isEqualTo(occurrence);
        assertThat(referralRequest.getAuthoredOnElement()).isEqualToComparingFieldByField(DateUtil.parse(AUTHOR_TIME));
        assertThat(deviceRef.getReference()).isEqualTo(referralRequest.getRequester().getAgent().getReference());
        assertThat(serviceProviderRef.getReference()).isEqualTo(referralRequest.getRequester().getOnBehalfOf().getReference());
        assertThat(new Reference(encounter).getReference()).isEqualTo(referralRequest.getContext().getReference());
        assertThat(patientRef.getReference()).isEqualTo(referralRequest.getSubject().getReference());
        assertThat(referralRequest.getSupportingInfo().get(0).getResource()).isEqualTo(procedureRequest);
        assertThat(referralRequest.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
        assertThat(referralRequest.getRecipient().get(0).getResource()).isInstanceOf(Practitioner.class);
        verify(practitionerMapper, times(1)).mapPractitioner(associatedEntity);
        verifyNoMoreInteractions(practitionerMapper);
    }
}