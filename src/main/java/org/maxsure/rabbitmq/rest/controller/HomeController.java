package org.maxsure.rabbitmq.rest.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.maxsure.rabbitmq.rest.amqp.ScopedRabbitMQEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/rest/v0.1")
@Slf4j
public class HomeController {

    private final ScopedRabbitMQEndpoint rabbitMQEndpoint;

    public HomeController(ScopedRabbitMQEndpoint rabbitMQEndpoint) {
        this.rabbitMQEndpoint = Preconditions.checkNotNull(rabbitMQEndpoint, "rabbitMQEndpoint");
    }

    @PutMapping("/publish")
    public Future<ResponseEntity<String>> publish(
            @RequestHeader Map<String, String> headers,
            @RequestParam("routing-key") String routingKey,
            @RequestBody byte[] data) {
        int deliveryMode;
        try {
            deliveryMode = Integer.valueOf(headers.getOrDefault("deliveryMode", "1"));
        } catch (Exception e) {
            deliveryMode = 1;
        }
        String correlationId = headers.get("correlationId");
        String replyTo = headers.get("replyTo");
        Date timestamp = new Date();
        String contentEncoding = headers.getOrDefault("contentEncoding", "UTF-8");
        String type = headers.get("type");

        Map<String, Object> msgHeaders = getAMQPMessageHeaders(headers, "header-");

        BasicProperties properties = MessageProperties.TEXT_PLAIN.builder()
                .deliveryMode(deliveryMode)
                .correlationId(correlationId)
                .replyTo(replyTo)
                .timestamp(timestamp)
                .contentEncoding(contentEncoding)
                .type(type)
                .headers(msgHeaders)
                .build();

        return CompletableFuture.supplyAsync(() -> {
            rabbitMQEndpoint.publish(routingKey, data, properties);
            String response = String.format("Published to: [%s]%n", routingKey);
            return ResponseEntity.ok().body(response);
        });
    }

    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> subscribe(@RequestParam("routing-key") String routingKey) {
        log.debug("Subscribed [{}]\n", routingKey);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").withZone(ZoneId.systemDefault());
        Flux<String> flux = rabbitMQEndpoint.subscribe(routingKey);
        return flux.map(elem -> ServerSentEvent.<String>builder()
                .id(uuid())
                .event("subscribe " + routingKey)
                .data(elem)
                .comment(formatter.format(Instant.now()))
                .build());
    }

    private Map<String, Object> getAMQPMessageHeaders(Map<String, String> headers, String prefix) {
        int len = prefix.length();
        Map<String, Object> map = Maps.newLinkedHashMap();
        headers.forEach((key, value) -> {
            if (key.startsWith(prefix)) {
                String headerKey = key.substring(len);
                map.put(headerKey, value);
            }
        });
        return map;
    }

    private String uuid() {
        return UUID.randomUUID().toString();
    }

}
