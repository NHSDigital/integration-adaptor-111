package uk.nhs.adaptors.oneoneone.config;

import java.util.Objects;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

@Configuration
public class AmqpConfiguration {

    private static final String RABBIT_MQ_VERSION_IDENTIFIER = "0-9-1";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public Destination jmsDestination(AmqpProperties properties) {

        if (Objects.equals(properties.getProtocol(), RABBIT_MQ_VERSION_IDENTIFIER)) {

            RMQDestination jmsDestination = new RMQDestination();
            jmsDestination.setAmqpExchangeName(properties.getExchange());
            jmsDestination.setAmqp(true);
            jmsDestination.setAmqpRoutingKey(properties.getQueueName());

            return jmsDestination;
        } else {
            return new JmsQueue(properties.getQueueName());
        }
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory(AmqpProperties properties) {

        if (Objects.equals(properties.getProtocol(), RABBIT_MQ_VERSION_IDENTIFIER)) {

            RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
            connectionFactory.setUsername(properties.getUsername());
            connectionFactory.setPassword(properties.getPassword());
            connectionFactory.setVirtualHost("/");
            connectionFactory.setHost(properties.getBroker() + properties.getPort());
            connectionFactory.setPort(properties.getPort());
            connectionFactory.setSsl(properties.isSslEnabled());

            return connectionFactory;
        }

        JmsConnectionFactory factory = new JmsConnectionFactory();
        factory.setRemoteURI(properties.getBroker() + properties.getPort());

        // These should never be null
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());

        return factory;

    }
}