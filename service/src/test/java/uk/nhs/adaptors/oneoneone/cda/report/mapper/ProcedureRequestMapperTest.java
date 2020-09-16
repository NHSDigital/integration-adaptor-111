package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01EncompassingEncounter;

@ExtendWith(MockitoExtension.class)
public class ProcedureRequestMapperTest {
    @InjectMocks
    private ProcedureRequestMapper procedureRequestMapper;
    @Mock
    private POCDMT000002UK01ClinicalDocument1 clinicalDocument1;
    @Mock
    private POCDMT000002UK01Component1 component1;
    @Mock
    private POCDMT000002UK01EncompassingEncounter encompassingEncounter;
    @Mock
    private CE ce;

    @Test
    public void patientMapperTest() {
        String displayName = "Go to the nearest Emergency Department within 4 hours.";
        String code = "Dx009";
        String codesystem = "2.16.840.1.113883.2.1.3.2.4.17.325";

        when(clinicalDocument1.isSetComponentOf()).thenReturn(true);
        when(clinicalDocument1.getComponentOf()).thenReturn(component1);
        when(component1.getEncompassingEncounter()).thenReturn(encompassingEncounter);
        when(encompassingEncounter.isSetDischargeDispositionCode()).thenReturn(true);
        when(encompassingEncounter.getDischargeDispositionCode()).thenReturn(ce);
        when(ce.isSetDisplayName()).thenReturn(true);
        when(ce.getDisplayName()).thenReturn(displayName);
        when(ce.isSetCode()).thenReturn(true);
        when(ce.getCode()).thenReturn(code);
        when(ce.isSetCodeSystem()).thenReturn(true);
        when(ce.getCodeSystem()).thenReturn(codesystem);

        ProcedureRequest procedureRequest = procedureRequestMapper.mapProcedureRequest(clinicalDocument1);

        assertThat(procedureRequest.getStatus()).isEqualTo(ProcedureRequest.ProcedureRequestStatus.NULL);
        assertThat(procedureRequest.getIntent()).isEqualTo(ProcedureRequest.ProcedureRequestIntent.NULL);
        assertThat(procedureRequest.getPriority()).isEqualTo(ProcedureRequest.ProcedureRequestPriority.NULL);
        assertThat(procedureRequest.getCode().getCoding().get(0).getDisplay()).isEqualTo(displayName);
        assertThat(procedureRequest.getCode().getCoding().get(0).getCode()).isEqualTo(code);
        assertThat(procedureRequest.getCode().getCoding().get(0).getSystem()).isEqualTo(codesystem);
    }
}
