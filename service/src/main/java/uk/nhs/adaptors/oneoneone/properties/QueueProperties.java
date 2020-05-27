package uk.nhs.adaptors.oneoneone.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties("queue")
public class QueueProperties {
    private String routingKey;
    private String exchange;
}
