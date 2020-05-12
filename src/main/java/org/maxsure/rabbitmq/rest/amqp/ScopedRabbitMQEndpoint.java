package org.maxsure.rabbitmq.rest.amqp;

import com.rabbitmq.client.BasicProperties;
import reactor.core.publisher.Flux;

public interface ScopedRabbitMQEndpoint {

    void publish(String routingKey, byte[] data, BasicProperties properties);

    Flux<String> subscribe(String routingKey);

}
