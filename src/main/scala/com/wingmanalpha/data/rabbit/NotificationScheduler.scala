/*
 * Copyright (c) 2019 Telefonica Innovacion Alpha. All rights reserved.
 */

package com.wingmanalpha.data.rabbit

import java.time.{Clock, LocalDateTime}

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{Channel, Connection}
import com.wingmanalpha.data.rabbit.tutorial.RabbitMQClient

import scala.collection.JavaConversions._
import scala.util.Random

object NotificationScheduler extends App with NotificationsConfig {

  /**
    * Only when expired messages reach the head of a queue will they actually be discarded (or dead-lettered)
    * Source: https://www.rabbitmq.com/ttl.html#per-message-ttl
    */

  val random = new Random()
  implicit val clock: Clock = Clock.systemDefaultZone()

  def publishMessage(queueName: String)(message: String, expirationTime: LocalDateTime, delay: Int)(implicit channel: Channel, clock: Clock): Unit = {
    val headers = Map(
      "expiration-time" -> expirationTime.toString,
      "times-seen" -> "0"
    )

    val messageProps: BasicProperties = new BasicProperties.Builder()
      .headers(headers)
      .expiration((delay * 1000).toString) // expiration time in milliseconds
      .build()

    channel.basicPublish("", queueName, messageProps, message.getBytes("UTF-8"))
    println(s" [x] Sent '$message'")
  }

  def messageProducer(queueName: String)(implicit channel: Channel, clock: Clock): Unit = {
    val messagesToSend = 4
    val publish: (String, LocalDateTime, Int) => Unit = publishMessage(queueName)

    (1 to messagesToSend).foreach { i =>
      val secondsDelay = random.nextInt(10)
      val expirationTime = LocalDateTime.now(clock).plusSeconds(secondsDelay)
      val message = s"Notification [$i] - [$secondsDelay] - [$expirationTime]"

      publish(message, expirationTime, secondsDelay)
    }
  }

  def messageProducerInfinite(queueName: String, secondsDelay: Int, sleepMillisDelay: Int)(implicit channel: Channel, clock: Clock): Unit = {
    val publish: (String, LocalDateTime, Int) => Unit = publishMessage(queueName)

    var i = 1
    while (true) {
      val messageDelay = random.nextInt(secondsDelay)
      val expirationTime = LocalDateTime.now(clock).plusSeconds(messageDelay)
      val message = s"Notification [$i] - [$messageDelay] - [$expirationTime]"

      publish(message, expirationTime, secondsDelay)
      Thread.sleep(sleepMillisDelay)
      i += 1
    }
  }

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    implicit val channel: Channel = connection.createChannel()

    declareQueues

    //        messageProducer(NotificationsWithDelay)
    messageProducerInfinite(NotificationsWithDelay, secondsDelay = 120, sleepMillisDelay = 100)

    channel.close()
    connection.close()
  }

  run()
}
