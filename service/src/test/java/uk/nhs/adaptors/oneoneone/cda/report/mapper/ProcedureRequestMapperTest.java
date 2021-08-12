package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;

@ExtendWith(MockitoExtension.class)
public class ProcedureRequestMapperTest {
    private static final String DISPLAY_NAME = "Go to the nearest Emergency Department within 4 hours.";
    private static final String CODE = "Dx009";
    private static final String CODESYSTEM = "2.16.840.1.113883.2.1.3.2.4.17.325";
    private static final String RANDOM_UUID = "12345678:ABCD:ABCD:ABCD:ABCD1234EFGH";

    private List<Reference> reasonReferenceList = new ArrayList<>();

    @InjectMocks
    private ProcedureRequestMapper procedureRequestMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument1;
    @Mock
    private POCDMT000002UK01Component1 component1;
    @Mock
    private POCDMT000002UK01EncompassingEncounter encompassingEncounter;
    @Mock
    private Reference patient;
    @Mock
    private CE ce;
    @Mock
    private ReferralRequest referralRequest;
    @Mock
    private Reference reasonReference;
    @Mock
    private Period occurence;
    @Mock
    private ResourceUtil resourceUtil;

    @BeforeEach
    public void setUp() {
        reasonReferenceList.add(reasonReference);

        when(clinicalDocument1.isSetComponentOf()).thenReturn(true);
        when(clinicalDocument1.getComponentOf()).thenReturn(component1);
        when(component1.getEncompassingEncounter()).thenReturn(encompassingEncounter);
        when(encompassingEncounter.isSetDischargeDispositionCode()).thenReturn(true);
        when(encompassingEncounter.getDischargeDispositionCode()).thenReturn(ce);
        when(ce.isSetDisplayName()).thenReturn(true);
        when(ce.getDisplayName()).thenReturn(DISPLAY_NAME);
        when(ce.isSetCode()).thenReturn(true);
        when(ce.getCode()).thenReturn(CODE);
        when(ce.isSetCodeSystem()).thenReturn(true);
        when(ce.getCodeSystem()).thenReturn(CODESYSTEM);
        when(referralRequest.getOccurrence()).thenReturn(occurence);
        when(referralRequest.getReasonReference()).thenReturn(reasonReferenceList);
        when(resourceUtil.newRandomUuid()).thenReturn(new IdType(RANDOM_UUID));
    }

    @Test
    public void patientMapperTest() {
        ProcedureRequest procedureRequest = procedureRequestMapper.mapProcedureRequest(clinicalDocument1, patient,
            referralRequest);

        assertThat(procedureRequest.getStatus()).isEqualTo(ProcedureRequest.ProcedureRequestStatus.ACTIVE);
        assertThat(procedureRequest.getIntent()).isEqualTo(ProcedureRequest.ProcedureRequestIntent.PLAN);
        assertThat(procedureRequest.getPriority()).isEqualTo(ProcedureRequest.ProcedureRequestPriority.ROUTINE);
        assertThat(procedureRequest.getCode().getCoding().get(0).getDisplay()).isEqualTo(DISPLAY_NAME);
        assertThat(procedureRequest.getCode().getCoding().get(0).getCode()).isEqualTo(CODE);
        assertThat(procedureRequest.getCode().getCoding().get(0).getSystem()).isEqualTo(CODESYSTEM);
        assertThat(procedureRequest.getSubject()).isEqualTo(patient);
        assertThat(procedureRequest.getDoNotPerform()).isEqualTo(false);
        assertThat(procedureRequest.getOccurrence()).isEqualTo(occurence);
        assertThat(procedureRequest.getReasonReference()).isEqualTo(reasonReferenceList);
        assertThat(procedureRequest.getIdElement().getValue()).isEqualTo(RANDOM_UUID);
    }
}
