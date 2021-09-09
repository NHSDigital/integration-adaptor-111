package uk.nhs.adaptors.oneoneone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import ca.uhn.fhir.context.FhirContext;
import uk.nhs.adaptors.oneoneone.cda.report.validation.ItkPropertiesValidator;

@Configuration
public class OneOneOneSpringConfiguration {

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forDstu3();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public static ItkPropertiesValidator configurationPropertiesValidator(RestTemplate restTemplate) {
        return new ItkPropertiesValidator(restTemplate);
    }
}
