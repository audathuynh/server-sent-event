package org.maxsure.rabbitmq.rest.amqp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageBuilderSupport;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;
import com.rabbitmq.client.BasicProperties;

@Component
public class SpringRabbitMQPublisher implements RabbitMQPublisher {

    private final RabbitTemplate rabbitTemplate;

    public SpringRabbitMQPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = Preconditions.checkNotNull(rabbitTemplate, "rabbitTemplate");
    }

    @Override
    public void publish(String exchangName,
            String routingKey,
            byte[] data,
            BasicProperties properties) {
        MessageDeliveryMode deliveryMode =
                MessageDeliveryMode.fromInt(properties.getDeliveryMode());
        MessageBuilderSupport<Message> messageBuilder = MessageBuilder.withBody(data)
                .setContentType(properties.getContentType())
                .setDeliveryMode(deliveryMode)
                .setCorrelationId(properties.getCorrelationId())
                .setReplyTo(properties.getReplyTo())
                .setTimestamp(properties.getTimestamp())
                .setContentEncoding(properties.getContentEncoding())
                .setType(properties.getType());
        properties.getHeaders().forEach(messageBuilder::setHeader);
        Message message = messageBuilder.build();

        rabbitTemplate.send(exchangName, routingKey, message);
    }

}
