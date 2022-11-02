package uk.nhs.adaptors.oneoneone.config;

import java.util.Objects;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;

import org.apache.commons.lang3.StringUtils;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import ca.uhn.fhir.util.StringUtil;

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
            jmsDestination.setAmqpRoutingKey(properties.getAmqpRoutingKey());
            jmsDestination.setAmqpQueueName(properties.getQueueName());

            return jmsDestination;
        }

        return new JmsQueue(properties.getQueueName());

    }

    @Bean
    public ConnectionFactory jmsConnectionFactory(AmqpProperties properties) {

        // As the current release version of 111 used the broker property as a whole,
        // we will not change this introducing a breaking change to existing released

        var remoteURI = properties.getBroker();

        if (Objects.equals(properties.getProtocol(), RABBIT_MQ_VERSION_IDENTIFIER)) {

            // However the broker in 0-9-1 requires the broker to be seperated from a url therefore we shall extract the part
            // between the // and : part sof the url.

            var brokerSlashes = properties.getBroker().indexOf("://");
            var brokerPortDivider = StringUtils.ordinalIndexOf(properties.getBroker(), ":", 2);
            var brokerAddress = properties.getBroker().substring(brokerSlashes+3,brokerPortDivider);

            RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
            connectionFactory.setUsername(properties.getUsername());
            connectionFactory.setPassword(properties.getPassword());
            connectionFactory.setVirtualHost("/");
            connectionFactory.setHost(brokerAddress);
            connectionFactory.setPort(properties.getPort());
            connectionFactory.setSsl(properties.isSslEnabled());

            return connectionFactory;
        }

        JmsConnectionFactory factory = new JmsConnectionFactory();
        factory.setRemoteURI(remoteURI);

        // These should never be null
        factory.setUsername(properties.getUsername());
        factory.setPassword(properties.getPassword());

        return factory;

    }
}