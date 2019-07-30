package com.afranzi.data.rabbit

import java.time.{Clock, LocalDateTime}

import com.afranzi.data.rabbit.tutorial.RabbitMQClient
import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.{Channel, Connection}

import scala.collection.JavaConversions._
import scala.util.Random

object NotificationDelayer extends App with NotificationsConfig {

  val random = new Random()
  implicit val clock: Clock = Clock.systemDefaultZone()

  def publishMessageWithDelay(exchangeQueue: String)(message: String, expirationTime: LocalDateTime, delay: Int)(implicit channel: Channel, clock: Clock): Unit = {
    val headers: Map[String, AnyRef] = Map(
      "expiration-time" -> expirationTime.toString,
      "times-seen" -> "0",
      "x-delay" -> (delay * 1000).toString
    )

    val messageProps: BasicProperties = new BasicProperties.Builder()
      .headers(headers)
      .build()

    channel.basicPublish(exchangeQueue, "", messageProps, message.getBytes("UTF-8"))
    println(s" [x] Sent '$message' with ${delay}s")
  }

  def produceMessage(publish: (String, LocalDateTime, Int) => Unit, index: Int, secondsDelay: Int): Unit = {
    val messageDelay = random.nextInt(secondsDelay)
    val expirationTime = LocalDateTime.now(clock).plusSeconds(messageDelay)
    val message = s"Notification ID:[$index] - Delay:[${messageDelay}s] - [$expirationTime]"

    publish(message, expirationTime, messageDelay)
  }

  def messageProducer(queueName: String, secondsDelay: Int, sleepMillisDelay: Int)(implicit channel: Channel, clock: Clock): Unit = {
    val messagesToSend = 4
    val publish: (String, LocalDateTime, Int) => Unit = publishMessageWithDelay(queueName)

    (1 to messagesToSend).foreach { i =>
      produceMessage(publish, index = i, secondsDelay = secondsDelay)
      Thread.sleep(sleepMillisDelay)
    }
  }

  def messageProducerInfinite(queueName: String, secondsDelay: Int, sleepMillisDelay: Int)(implicit channel: Channel, clock: Clock): Unit = {
    val publish: (String, LocalDateTime, Int) => Unit = publishMessageWithDelay(queueName)

    var i = 1
    while (true) {
      produceMessage(publish, i, secondsDelay)
      Thread.sleep(sleepMillisDelay)
      i += 1
    }
  }

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    implicit val channel: Channel = connection.createChannel()

    declareQueues

    //    messageProducer(NotificationsExchangeDelay, secondsDelay = 10, sleepMillisDelay = 100)
    messageProducerInfinite(NotificationsExchangeDelay, secondsDelay = 300, sleepMillisDelay = 10)

    channel.close()
    connection.close()
  }

  run()
}
