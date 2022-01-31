package uk.nhs.adaptors.containers;

import org.testcontainers.containers.GenericContainer;

public final class RabbitMqContainer extends GenericContainer<RabbitMqContainer> {

    public static final int ACTIVEMQ_PORT = 5672;
    private static RabbitMqContainer container;

    private RabbitMqContainer() {
        super("rabbitmq:latest");
        addExposedPort(ACTIVEMQ_PORT);
    }

    public static RabbitMqContainer getInstance() {
        if (container == null) {
            container = new RabbitMqContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("PEM111_AMQP_BROKER", getContainerIpAddress());
        System.setProperty("PEM111_AMQP_USERNAME", "guest");
        System.setProperty("PEM111_AMQP_PASSWORD", "guest");
        System.setProperty("PEM111_AMQP_PORT", String.valueOf(getMappedPort(ACTIVEMQ_PORT)));
        System.setProperty("PEM111_AMQP_PROTOCOL", "0-9-1");
        System.setProperty("PEM111_AMQP_SSL_ENABLED", "false");
    }
}