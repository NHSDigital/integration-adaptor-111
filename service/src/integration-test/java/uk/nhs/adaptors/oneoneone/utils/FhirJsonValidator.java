package uk.nhs.adaptors.oneoneone.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.SchemaBaseValidator;
import ca.uhn.fhir.validation.ValidationResult;

@Component
public class FhirJsonValidator {

    @Autowired
    private FhirContext ctx;

    public boolean isValid(String message) {
        FhirValidator validator = ctx.newValidator();
        IValidatorModule module = new SchemaBaseValidator(ctx);
        validator.registerValidatorModule(module);
        ValidationResult result = validator.validateWithResult(message);
        return result.getMessages().isEmpty();
    }
}
