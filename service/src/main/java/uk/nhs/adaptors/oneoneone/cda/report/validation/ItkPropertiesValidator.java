package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;

@Slf4j
public class ItkPropertiesValidator implements Validator {

    public boolean supports(Class clazz) {
        return ItkProperties.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        ItkProperties itkProperties = (ItkProperties) target;
        if (itkProperties.getUrl().isEmpty() && isEmpty(itkProperties.getOdsCodes()) && isEmpty(itkProperties.getDosIds())) {
            LOGGER.error("Url, odsCodes and dosIds properties are empty");
            errors.reject("1");
        }
    }
}
