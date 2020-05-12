package org.maxsure.rabbitmq.rest.amqp;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Delivery;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class SpringRabbitMQSubscriber implements RabbitMQSubscriber {

    private final Connection connection;
    private final MessageMarshaller messageMarshaller;

    public SpringRabbitMQSubscriber(
            ConnectionFactory connectionFactory,
            MessageMarshaller messageMarshaller) {
        Preconditions.checkNotNull(connectionFactory, "connectionFactory");
        this.connection = connectionFactory.createConnection();
        this.messageMarshaller = Preconditions.checkNotNull(messageMarshaller, "messageMarshaller");
    }

    @Override
    public Flux<String> subscribe(String exchangeName, String routingKey) {
        try (Channel channel = connection.createChannel(false)) {
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);

            return Flux.<String>create(sink -> {
                try {
                    channel.basicConsume(queueName, true,
                            (consumerTag, delivery) -> {
                                String data = buildResult(delivery);
                                log.debug("received {}", new String(delivery.getBody()));
                                sink.next(data);
                            },
                            consumerTag -> log.debug("Cancelled with tag: {}",
                                    consumerTag));
                } catch (IOException e) {
                    log.error("Error when consuming messages", e);
                    sink.error(e);
                }
            }).doFinally(sinalType -> {
                try {
                    channel.queueDelete(queueName);
                    channel.close();
                } catch (IOException | TimeoutException e) {
                    log.error("Error when finalising the stream", e);
                }
            });
        } catch (AmqpException | TimeoutException | IOException e) {
            log.error("Error when subscribing", e);
            return Flux.<String>empty();
        }
    }

    private String buildResult(Delivery delivery) {
        return messageMarshaller.marshall(delivery);
    }

}
