package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import uk.nhs.adaptors.oneoneone.config.ItkProperties;

public class ItkPropertiesValidator implements Validator {

    public boolean supports(Class clazz) {
        return ItkProperties.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        ItkProperties itkProperties = (ItkProperties) target;
        if (itkProperties.getUrl().isEmpty() && isEmpty(itkProperties.getOdsCodes()) && isEmpty(itkProperties.getDosIds())) {
            errors.reject("1", "Url, odsCodes and dosIds properties are empty"); // jak sprawic zeby to sie wypisywalo?
        }
    }
}
