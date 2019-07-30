# RabbitMQ-PoC
This PoC aims to investigate how to handle a notification system using RabbitMQ and delayed/expired queues.

## Run

```
# Build ignoring tests
sbt 'set test in assembly := {}' clean assembly

# Execute NotificationScheduler
java -cp target/scala-2.11/rabbit-mq-assembly-0.0.1-SNAPSHOT.jar  com.afranzi.data.rabbit.NotificationScheduler

# Execute NotificationDelayer using the Delayed Message Exchange Plugin
java -cp target/scala-2.11/rabbit-mq-assembly-0.0.1-SNAPSHOT.jar  com.afranzi.data.rabbit.NotificationDelayer

# Execute NotificationConsumer
java -cp target/scala-2.11/rabbit-mq-assembly-0.0.1-SNAPSHOT.jar  com.afranzi.data.rabbit.NotificationConsumer
```


## Tutorials

- https://www.rabbitmq.com/getstarted.html


## Plugins to be taken into account

#### [Delayed Messaging for RabbitMQ](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange)

> A user can declare an exchange with the type x-delayed-message and then publish messages with the custom header x-delay expressing in milliseconds a delay time for the message. The message will be delivered to the respective queues after x-delay milliseconds.

## Links of Interest
- [Dead Letter Exchanges](https://www.rabbitmq.com/dlx.html) - Messages from a queue can be "dead-lettered"; that is, republished to an exchange when any of the following events occur:
    - The message is negatively acknowledged by a consumer using basic.reject or basic.nack with requeue parameter set to false.
    - The message expires due to per-message TTL
    - The message is dropped because its queue exceeded a length limit
- [Time-To-Live and Expiration](https://www.rabbitmq.com/ttl.html) - RabbitMQ allows you to set TTL (time to live) for both messages and queues.
