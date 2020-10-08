package uk.nhs.adaptors.oneoneone.cda.report.util;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

import lombok.experimental.UtilityClass;
import uk.nhs.connect.iucds.cda.ucr.CE;

@UtilityClass
public class CodeUtil {

    public static CodeableConcept createCodeableConceptList(CE ce) {
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setCode(ce.getCodeSystem());
        coding.setDisplay(ce.getDisplayName());
        coding.setSystem(ce.getCode());
        codeableConcept.addCoding(coding);

        return codeableConcept;
    }
}
