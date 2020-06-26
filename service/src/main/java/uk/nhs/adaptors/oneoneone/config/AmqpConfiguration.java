package uk.nhs.adaptors.oneoneone.config;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import static org.springframework.util.StringUtils.isEmpty;

@Configuration
public class AmqpConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public JmsConnectionFactory jmsConnectionFactory(AmqpProperties properties) {
        JmsConnectionFactory factory = new JmsConnectionFactory();

        factory.setRemoteURI(properties.getBroker());

        if (!isEmpty(properties.getUsername())) {
            factory.setUsername(properties.getUsername());
        }

        if (!isEmpty(properties.getPassword())) {
            factory.setPassword(properties.getPassword());
        }

        return factory;
    }
}