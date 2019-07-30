# RabbitMQ-PoC


## Run

```
# Build ignoring tests
sbt 'set test in assembly := {}' clean assembly

# Execute NotificationScheduler
java -cp target/scala-2.11/rabbit-mq-assembly-0.0.1-SNAPSHOT.jar  com.afranzi.data.rabbit.NotificationScheduler

# Execute NotificationConsumer
java -cp target/scala-2.11/rabbit-mq-assembly-0.0.1-SNAPSHOT.jar  com.afranzi.data.rabbit.NotificationConsumer
```


## Tutorials

- https://www.rabbitmq.com/getstarted.html