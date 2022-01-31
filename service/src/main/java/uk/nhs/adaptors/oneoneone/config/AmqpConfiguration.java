package uk.nhs.adaptors.oneoneone.config;

import static org.springframework.util.StringUtils.isEmpty;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import java.util.Objects;

@Configuration
public class AmqpConfiguration {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public Destination jmsDestination(AmqpProperties properties) {

        if(Objects.equals(properties.getProtocol(), "0-9-1")) {

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

        if(Objects.equals(properties.getProtocol(), "0-9-1")) {

            RMQConnectionFactory connectionFactory = new RMQConnectionFactory();
            connectionFactory.setUsername(properties.getUsername());
            connectionFactory.setPassword(properties.getPassword());
            connectionFactory.setVirtualHost("/");
            connectionFactory.setHost(properties.getBroker());
            connectionFactory.setPort(properties.getPort());
            connectionFactory.setSsl(properties.isSslEnabled());

            return connectionFactory;
        } else {

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


}