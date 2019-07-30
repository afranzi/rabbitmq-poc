package com.afranzi.data.rabbit

import java.time.Clock

import com.afranzi.data.rabbit.tutorial.RabbitMQClient
import com.rabbitmq.client._

object NotificationConsumer extends App with NotificationsConfig {

  implicit val clock: Clock = Clock.systemDefaultZone()

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    implicit val channel: Channel = connection.createChannel()

    declareQueues
    channel.basicQos(1000)
    channel.queueBind(NotificationsToSend, NotificationsReady, "#")

    val queueConsumer = new QueueConsumer
    queueConsumer.consumeQueue(NotificationsToSend)
    queueConsumer.checkDelayedQueue(NotificationsWithDelay)
  }

  run()

}
