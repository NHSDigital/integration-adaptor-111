package uk.nhs.adaptors.oneoneone.cda.report.validation;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.adaptors.oneoneone.config.ItkProperties;
import uk.nhs.adaptors.oneoneone.config.OdsCodesDosIds;

@Slf4j
@RequiredArgsConstructor
@Component
public class ItkPropertiesValidator implements Validator {
    private final RestTemplate restTemplate;

    public boolean supports(Class clazz) {
        return ItkProperties.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
//        ItkProperties itkProperties = (ItkProperties) target;
//        if (!isAtLeastOneItkPropertySet(itkProperties)) {
//            errors.reject("1", "Configuration Error. "
//                + "At least one of PEM111_ITK_ODS_CODE_LIST, PEM111_ITK_DOS_ID_LIST, PEM111_ITK_EXTERNAL_CONFIGURATION_URL itk properties"
//                + " has to be set.");
//            return;
//        }
//        String url = itkProperties.getExternalConfigurationServiceUrl();
//        if (isNotEmpty(url)) {
//            if (!isExternalConfigurationServiceUp(url)) {
//                errors.reject("1", "Failed to retrieve configuration from external server: " + url);
//            }
//        }
    }

    private boolean isAtLeastOneItkPropertySet(ItkProperties itkProperties) {
        return isNotEmpty(itkProperties.getExternalConfigurationServiceUrl())
            || !isEmpty(itkProperties.getOdsCodes())
            || !isEmpty(itkProperties.getDosIds());
    }

    private boolean isExternalConfigurationServiceUp(String externalServiceUrl) {
        try {
            restTemplate.getForEntity(externalServiceUrl, OdsCodesDosIds.class);
            return true;
        } catch (RestClientException exception) {
            return false;
        }
    }
}
