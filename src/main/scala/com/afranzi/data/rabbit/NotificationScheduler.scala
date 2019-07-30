package com.afranzi.data.rabbit

import java.time.Clock

import com.afranzi.data.rabbit.tutorial.RabbitMQClient
import com.rabbitmq.client.{Channel, Connection}

import scala.util.Random

object NotificationScheduler extends App with NotificationsConfig {

  /**
    * Only when expired messages reach the head of a queue will they actually be discarded (or dead-lettered)
    * Source: https://www.rabbitmq.com/ttl.html#per-message-ttl
    */

  implicit val random: Random = new Random()
  implicit val clock: Clock = Clock.systemDefaultZone()

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    implicit val channel: Channel = connection.createChannel()

    declareQueues

    val queuePublisher = new QueuePublisherWithExpiration()
    queuePublisher.messageProducerInfinite(NotificationsExchangeDelay, secondsDelay = 300, sleepMillisDelay = 10)

    channel.close()
    connection.close()
  }

  run()
}
