package uk.nhs.adaptors.oneoneone.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "amqp")
@Getter
@Setter
public class AmqpProperties {
    private String broker;
    private String username;
    private String password;
    private String queueName;
}