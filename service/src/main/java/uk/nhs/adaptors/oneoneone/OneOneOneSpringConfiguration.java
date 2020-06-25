package uk.nhs.adaptors.oneoneone;

import ca.uhn.fhir.context.FhirContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OneOneOneSpringConfiguration {

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forDstu3();
    }
}
