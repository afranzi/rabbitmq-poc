package com.afranzi.data.rabbit

import java.time.temporal.ChronoUnit.MILLIS
import java.time.{Clock, LocalDateTime}

import com.afranzi.data.rabbit.Utils._
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{Channel, DeliverCallback, Delivery}

import scala.collection.JavaConversions._

class QueueConsumer(implicit clock: Clock) {

  def consumeQueue(queueName: String)(implicit channel: Channel): Unit = {
    val deliverCallback: DeliverCallback = new DeliverCallback {
      def handle(consumerTag: String, delivery: Delivery): Unit = {
        val message = new String(delivery.getBody, "UTF-8")
        val headers: Map[String, AnyRef] = delivery.getProperties.getHeaders.toMap

        val expirationTime = getHeader("expiration-time", headers)
        val expectedTime: LocalDateTime = LocalDateTime.parse(expirationTime)
        val now = LocalDateTime.now()
        val delay = MILLIS.between(expectedTime, now)

        println(s" [x] Received - '$message' - with a delay of [${delay}ms]")

        val deliveryTag = delivery.getEnvelope.getDeliveryTag
        channel.basicAck(deliveryTag, false)
      }
    }

    channel.basicConsume(queueName, false, deliverCallback, genericCancelCallback)
  }

  def checkDelayedQueue(queueName: String)(implicit channel: Channel): Unit = {
    val deliverCallback: DeliverCallback = new DeliverCallback {

      def handle(consumerTag: String, delivery: Delivery): Unit = {
        val headers: Map[String, AnyRef] = delivery.getProperties.getHeaders.toMap

        val now = LocalDateTime.now(clock)
        val expirationTime = getHeader("expiration-time", headers)
        val expectedTime: LocalDateTime = LocalDateTime.parse(expirationTime)

        val timesSeen = getHeader("times-seen", headers).toInt + 1
        val newHeaders: Map[String, AnyRef] = Map("times-seen" -> timesSeen.toString)

        val nextDelay = MILLIS.between(now, expectedTime)
        val messageProps: BasicProperties = new BasicProperties.Builder()
          .headers(headers ++ newHeaders)
          .expiration(nextDelay.toString) // expiration time in milliseconds
          .build()

        val deliveryTag = delivery.getEnvelope.getDeliveryTag
        if (nextDelay >= 0) {
          channel.basicPublish("", queueName, messageProps, delivery.getBody)
          channel.basicAck(deliveryTag, false)
        } else {
          channel.basicNack(deliveryTag, false, true)
        }
      }
    }

    channel.basicConsume(queueName, false, deliverCallback, genericCancelCallback)
  }

}
