package com.afranzi.data.rabbit

import java.time.{Clock, LocalDateTime}

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel

import scala.collection.JavaConversions._
import scala.util.Random

trait QueuePublisher {

  val clock: Clock
  val channel: Channel
  val random: Random

  def publishMessage(queueName: String)(message: String, expirationTime: LocalDateTime, delay: Int): Unit

  def produceMessage(publish: (String, LocalDateTime, Int) => Unit, index: Int, secondsDelay: Int): Unit = {
    val messageDelay = random.nextInt(secondsDelay)
    val expirationTime = LocalDateTime.now(clock).plusSeconds(messageDelay)
    val message = s"Notification ID:[$index] - Delay:[${messageDelay}s] - [$expirationTime]"

    publish(message, expirationTime, messageDelay)
  }

  def messageProducer(queueName: String, secondsDelay: Int, sleepMillisDelay: Int): Unit = {
    val messagesToSend = 4
    val publish: (String, LocalDateTime, Int) => Unit = publishMessage(queueName)

    (1 to messagesToSend).foreach { i =>
      produceMessage(publish, index = i, secondsDelay = secondsDelay)
      Thread.sleep(sleepMillisDelay)
    }
  }

  def messageProducerInfinite(queueName: String, secondsDelay: Int, sleepMillisDelay: Int): Unit = {
    val publish: (String, LocalDateTime, Int) => Unit = publishMessage(queueName)

    var i = 1
    while (true) {
      produceMessage(publish, i, secondsDelay)
      Thread.sleep(sleepMillisDelay)
      i += 1
    }
  }

}

class QueuePublisherWithDelay(implicit val clock: Clock, val channel: Channel, val random: Random) extends QueuePublisher {

  /**
    * A user can declare an exchange with the type x-delayed-message and then publish messages
    * with the custom header x-delay expressing in milliseconds a delay time for the message.
    * The message will be delivered to the respective queues after x-delay milliseconds.
    * Source: https://github.com/rabbitmq/rabbitmq-delayed-message-exchange
    */

  def publishMessage(exchangeQueue: String)(message: String, expirationTime: LocalDateTime, delay: Int): Unit = {
    val headers: Map[String, AnyRef] = Map(
      "expiration-time" -> expirationTime.toString,
      "x-delay" -> (delay * 1000).toString
    )

    val messageProps: BasicProperties = new BasicProperties.Builder()
      .headers(headers)
      .build()

    channel.basicPublish(exchangeQueue, "", messageProps, message.getBytes("UTF-8"))
    println(s" [x] Sent '$message' with ${delay}s")
  }

}

class QueuePublisherWithExpiration(implicit val clock: Clock, val channel: Channel, val random: Random) extends QueuePublisher {

  /**
    * Only when expired messages reach the head of a queue will they actually be discarded (or dead-lettered)
    * Source: https://www.rabbitmq.com/ttl.html#per-message-ttl
    */

  def publishMessage(queueName: String)(message: String, expirationTime: LocalDateTime, delay: Int): Unit = {
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

}
