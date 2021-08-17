package uk.nhs.adaptors.oneoneone.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "itk")
@Setter
public class ItkProperties {
    private String odsCodes;
    private String dosIds;

    public List<String> getOdsCodes() {
        return Arrays.asList(odsCodes.split(","));
    }

    public List<String> getDosIds() {
        return Arrays.asList(dosIds.split(","));
    }
}
