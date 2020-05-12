package org.maxsure.rabbitmq.rest.amqp;

import reactor.core.publisher.Flux;

public interface RabbitMQSubscriber {

    Flux<String> subscribe(String exchangeName, String routingKey);

}
