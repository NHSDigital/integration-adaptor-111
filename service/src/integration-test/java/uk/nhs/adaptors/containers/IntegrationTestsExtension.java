package uk.nhs.adaptors.containers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;

import static org.springframework.jms.support.destination.JmsDestinationAccessor.RECEIVE_TIMEOUT_NO_WAIT;

@Slf4j
public class IntegrationTestsExtension implements BeforeAllCallback, BeforeEachCallback {

    public static final String DLQ_PREFIX = "DLQ.";

    @Override
    public void beforeAll(ExtensionContext context) {
        ActiveMqContainer.getInstance().start();
    }

    @Override
    @SuppressFBWarnings(
        value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
        justification = "SpotBugs issue with fix not yet released https://github.com/spotbugs/spotbugs/issues/456")
    public void beforeEach(ExtensionContext context) {
        var applicationContext = SpringExtension.getApplicationContext(context);

        var jmsTemplate = applicationContext.getBean(JmsTemplate.class);

        var meshInboundQueueName = Objects.requireNonNull(
            applicationContext.getEnvironment().getProperty("amqp.queueName"));

        var receiveTimeout = jmsTemplate.getReceiveTimeout();
        jmsTemplate.setReceiveTimeout(RECEIVE_TIMEOUT_NO_WAIT);
        List.of(meshInboundQueueName, DLQ_PREFIX + meshInboundQueueName)
            .forEach(queueName -> {
                while (jmsTemplate.receive(queueName) != null) {
                    LOGGER.info("Purged '" + queueName + "' message");
                }
            });
        jmsTemplate.setReceiveTimeout(receiveTimeout);
    }
}
