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