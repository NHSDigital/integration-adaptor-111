package uk.nhs.adaptors.oneoneone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties
@Getter
@Setter
public class DeviceProperties {
    private String version;
}
