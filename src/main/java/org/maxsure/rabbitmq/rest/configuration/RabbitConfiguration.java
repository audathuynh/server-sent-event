package org.maxsure.rabbitmq.rest.configuration;

import org.maxsure.rabbitmq.rest.amqp.RabbitMQPublisher;
import org.maxsure.rabbitmq.rest.amqp.RabbitMQSubscriber;
import org.maxsure.rabbitmq.rest.amqp.ScopedRabbitMQEndpoint;
import org.maxsure.rabbitmq.rest.amqp.ScopedRabbitMQEndpointImpl;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Bean
    public TopicExchange dataExchange(
            @Value("${org.maxsure.rabbitmq.exchange.name}") String exchangeName) {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public ScopedRabbitMQEndpoint scopedRabbitEndpoint(
            @Value("${org.maxsure.rabbitmq.exchange.name}") String exchangeName,
            RabbitMQPublisher publisher,
            RabbitMQSubscriber subscriber) {
        return new ScopedRabbitMQEndpointImpl(exchangeName, publisher, subscriber);
    }

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${org.maxsure.rabbitmq.url}") String serverIP,
            @Value("${org.maxsure.rabbitmq.port}") int port,
            @Value("${org.maxsure.rabbitmq.username}") String username,
            @Value("${org.maxsure.rabbitmq.password}") String password) {
        CachingConnectionFactory conectionFactory = new CachingConnectionFactory(serverIP);
        conectionFactory.setPort(port);
        conectionFactory.setUsername(username);
        conectionFactory.setPassword(password);
        return conectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            @Value("${org.maxsure.rabbitmq.exchange.name}") String exchangeName) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(exchangeName);
        return template;
    }

}
