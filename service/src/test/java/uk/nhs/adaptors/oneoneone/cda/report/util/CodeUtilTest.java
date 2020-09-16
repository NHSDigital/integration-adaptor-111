package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.when;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.nhs.connect.iucds.cda.ucr.CE;

@ExtendWith(MockitoExtension.class)
public class CodeUtilTest {
    @Mock
    private CE ce;

    @Test
    public void shouldCreateCodeableConcept() {
        String codeSystem = "code system";
        when(ce.getCodeSystem()).thenReturn(codeSystem);
        CodeableConcept codeableConcept = CodeUtil.createCodeableConceptList(ce);
        assertThat(codeableConcept.getCoding().get(0).getCode()).isEqualTo(codeSystem);
    }
}
