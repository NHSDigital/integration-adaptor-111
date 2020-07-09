package uk.nhs.adaptors.oneoneone.cda.report.enums;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;

public interface Concept {

    default CodeableConcept toCodeableConcept() {
        final var coding = toCoding();
        return new CodeableConcept().addCoding(coding).setText(coding.getDisplay());
    }
    default Coding toCoding() {
        return new Coding()
            .setCode(getValue())
            .setDisplay(getDisplay())
            .setSystem(getSystem());
    }
    String getValue();
    String getDisplay();
    String getSystem();
    default String toCode() {
        return getValue();
    }
}
