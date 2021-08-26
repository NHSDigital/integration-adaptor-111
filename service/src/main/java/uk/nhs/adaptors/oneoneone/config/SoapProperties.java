package uk.nhs.adaptors.oneoneone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "soap")
@Getter
@Setter
public class SoapProperties {
    private boolean validationEnabled;
    private String sendTo;
}
