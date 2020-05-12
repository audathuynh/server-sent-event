package org.maxsure.rabbitmq.rest.amqp;

import com.google.common.base.Preconditions;
import com.rabbitmq.client.BasicProperties;
import reactor.core.publisher.Flux;

public class ScopedRabbitMQEndpointImpl implements ScopedRabbitMQEndpoint {

    private final String exchangeName;
    private final RabbitMQPublisher publisher;
    private final RabbitMQSubscriber subscriber;

    public ScopedRabbitMQEndpointImpl(
            String exchangeName,
            RabbitMQPublisher publisher,
            RabbitMQSubscriber subscriber) {
        this.exchangeName = Preconditions.checkNotNull(exchangeName, "exchangeName");
        this.publisher = Preconditions.checkNotNull(publisher, "publisher");
        this.subscriber = Preconditions.checkNotNull(subscriber, "subscriber");
    }

    @Override
    public void publish(String routingKey, byte[] data, BasicProperties properties) {
        publisher.publish(exchangeName, routingKey, data, properties);
    }

    @Override
    public Flux<String> subscribe(String routingKey) {
        return subscriber.subscribe(exchangeName, routingKey);
    }

}
