package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import uk.nhs.connect.iucds.cda.ucr.CE;

@UtilityClass
public class CodeUtil {
    public final String FHIR_SNOMED = "http://snomed.info/sct";
    public final String ITK_SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";

    public CodeableConcept createCodeableConcept(List<Coding> codings) {
        return new CodeableConcept()
                .setCoding(codings)
                .setText(codings.stream()
                        .findFirst()
                        .map(Coding::getDisplay)
                        .orElse(null)
                );
    }

    public CodeableConcept createCodeableConceptFromCE(CE... codings) {
        return createCodeableConceptFromCE(List.of(codings));
    }

    public CodeableConcept createCodeableConceptFromCE(List<? extends CE> codings) {
        return createCodeableConcept(codings.stream()
                .map(CodeUtil::createCoding)
                .collect(Collectors.toUnmodifiableList()));
    }

    public Coding createCoding(CE code) {
        return new Coding(mapSystem(code.getCodeSystem()), code.getCode(), code.getDisplayName());
    }

    public String mapSystem(String codeSystem) {
        switch (codeSystem) {
            case ITK_SNOMED: return FHIR_SNOMED;
            default: return codeSystem;
        }
    }
}