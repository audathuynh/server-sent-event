package org.maxsure.rabbitmq.rest.configuration;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import org.maxsure.rabbitmq.rest.amqp.RabbitMQPublisher;
import org.maxsure.rabbitmq.rest.amqp.RabbitMQSubscriber;
import org.maxsure.rabbitmq.rest.amqp.ScopedRabbitMQEndpoint;
import org.maxsure.rabbitmq.rest.amqp.ScopedRabbitMQEndpointImpl;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RabbitConfiguration {

    @Bean
    public ConnectionFactory connectionFactory(
            @Value("${org.maxsure.rabbitmq.host}") String host,
            @Value("${org.maxsure.rabbitmq.port}") int port,
            @Value("${org.maxsure.rabbitmq.username}") String username,
            @Value("${org.maxsure.rabbitmq.password}") String password,
            @Value("${org.maxsure.rabbitmq.ssl.enabled:false}") boolean sslEnabled,
            @Value("${org.maxsure.rabbitmq.ssl.verification-enabled:false}") boolean verificationEnabled,
            @Value("${org.maxsure.rabbitmq.ssl.key-store.type:PKCS12}") String keyStoreType,
            @Value("${org.maxsure.rabbitmq.ssl.key-store.path}") String keyStorePath,
            @Value("${org.maxsure.rabbitmq.ssl.key-store.password}") String keyStorePassword,
            @Value("${org.maxsure.rabbitmq.ssl.trust-store.type:JKS}") String trustStoreType,
            @Value("${org.maxsure.rabbitmq.ssl.trust-store.path}") String trustStorePath,
            @Value("${org.maxsure.rabbitmq.ssl.trust-store.password}") String trustStorePassword,
            @Value("${org.maxsure.rabbitmq.ssl.security-algorithm:SunX509}") String securityAlgorithm,
            @Value("${org.maxsure.rabbitmq.ssl.version:TLSv1.2}") String sslVersion) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        if (sslEnabled) {
            com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory =
                    connectionFactory.getRabbitConnectionFactory();
            try {
                if (!verificationEnabled) {
                    rabbitConnectionFactory.useSslProtocol();
                } else {
                    KeyManagerFactory keyManagerFactory =
                            KeyManagerFactory.getInstance(securityAlgorithm);
                    char[] keyStorePasswordChars = keyStorePassword.toCharArray();
                    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                    try (InputStream keyStoreFileStream = new FileInputStream(keyStorePath)) {
                        keyStore.load(keyStoreFileStream, keyStorePasswordChars);
                    }
                    keyManagerFactory.init(keyStore, keyStorePasswordChars);

                    KeyStore trustStore = KeyStore.getInstance(trustStoreType);
                    try (InputStream trustStoreFileStream = new FileInputStream(trustStorePath)) {
                        trustStore.load(trustStoreFileStream, trustStorePassword.toCharArray());
                    }
                    TrustManagerFactory trustManagerFactory =
                            TrustManagerFactory.getInstance(securityAlgorithm);
                    trustManagerFactory.init(trustStore);

                    SSLContext context = SSLContext.getInstance(sslVersion);
                    context.init(keyManagerFactory.getKeyManagers(),
                            trustManagerFactory.getTrustManagers(), null);

                    rabbitConnectionFactory.useSslProtocol(context);
                    rabbitConnectionFactory.enableHostnameVerification();
                }
            } catch (Exception e) {
                log.error("Error when enabling SSL protocol for RabbitConnectionFactory", e);
            }
        }
        return connectionFactory;
    }

    @Bean("exchangeName")
    public String getExchangeName(
            @Value("${org.maxsure.rabbitmq.exchange-name}") String exchangeName) {
        return exchangeName;
    }

    @Bean
    public TopicExchange dataExchange(@Qualifier("exchangeName") String exchangeName) {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public ScopedRabbitMQEndpoint scopedRabbitEndpoint(
            @Qualifier("exchangeName") String exchangeName,
            RabbitMQPublisher publisher,
            RabbitMQSubscriber subscriber) {
        return new ScopedRabbitMQEndpointImpl(exchangeName, publisher, subscriber);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            @Qualifier("exchangeName") String exchangeName) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(exchangeName);
        return template;
    }

}
