# server-sent-event: A demo with RabbitMQ.

This is a demo to provide Rest APIs to allow users to publish and subscribe messages to RabbitMQ.

## Step 1: Run RabbitMQ

Run RabbitMQ on localhost, at the port 5672.

Username: guest

Password: guest

Reference: [RabbitMQ website](https://www.rabbitmq.com)

The configuration of RabbitMQ can be changed in the configuration file application.yaml.

Below is the default configuration in the application.

```
org:
    maxsure:
        rabbitmq:
            host: localhost
            port: 5672
            username: guest
            password: guest
            exchange:
              name: app.exchange.data
```
           


## Step 2: Run the app rabbitmqRest

Run the below command to start the app. By default, the app will run at the port 8080.
The default port can be changed in the file application.yaml.

```
$ java -jar rabbitmqRest-1.0.0.jar 
```

## Subscribe to receive messages with a routing-key

In the routing-key:

* `*` (star) can substitute for exactly one word.
* `#` (hash) can substitute for zero or more words.

Notes: The character `#` needs to be written as `%23` on the URL on browsers.

### Example 1

Subscribe to receive all messages from the routing key `a.b`.

```
http://localhost:8080/rest/v0.1/subscribe?routing-key=a.b
```

### Example 2

Subscribe to receive all messages from the routing keys with the prefix `a.` and exactly one word.

```
http://localhost:8080/rest/v0.1/subscribe?routing-key=a.*
```

### Example 3

Subscribe to receive all messages from the routing keys with the prefix `a.`.

```
http://localhost:8080/rest/v0.1/subscribe?routing-key=a.%23
```

## Publish a message with a routing-key

In the below examples, we assume that we only subscribe to receive messages with the routing keys in the previous section.

### Example 1

```
$ curl -X PUT -H "Content-Type: application/octet-stream" -d "Hello" http://localhost:8080/rest/v0.1/publish?routing-key=a.b
```
The message is sent to `a.b`, `a.*`, and `a.#`.

### Example 2

```
$ curl -X PUT -H "Content-Type: application/octet-stream" -d "Hello" http://localhost:8080/rest/v0.1/publish?routing-key=a.c
```
The message is sent to `a.*` and `a.#`. It is not sent to `a.b`.

### Example 3

```
$ curl -X PUT -H "Content-Type: application/octet-stream" -d "Hello" http://localhost:8080/rest/v0.1/publish?routing-key=a.b.c.d
```
The message is only sent to `a.#`. It is not sent to `a.b` and `a.*`.
