package org.maxsure.rabbitmq.rest.amqp;

import com.rabbitmq.client.BasicProperties;

public interface RabbitMQPublisher {

    void publish(String exchangName, String routingKey, byte[] data, BasicProperties properties);

}
