package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;

public class ReferenceMapper {

    public static Reference mapReference() {
        Reference reference = new Reference();
        reference.setDisplay("");
        reference.setDisplayElement(new StringType(""));
        reference.setIdentifier(new Identifier());
        reference.setReference("");
        reference.setReferenceElement(new StringType(""));
        return reference;
    }
}
