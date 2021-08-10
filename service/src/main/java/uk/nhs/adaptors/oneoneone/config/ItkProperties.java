package uk.nhs.adaptors.oneoneone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "itk")
@Getter
@Setter
public class ItkProperties {
    private String odsCodes;
    private String dosIds;
}
