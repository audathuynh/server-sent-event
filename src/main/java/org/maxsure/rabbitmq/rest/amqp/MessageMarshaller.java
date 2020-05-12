package org.maxsure.rabbitmq.rest.amqp;

import com.rabbitmq.client.Delivery;

public interface MessageMarshaller {

    String marshall(Delivery delivery);

}
