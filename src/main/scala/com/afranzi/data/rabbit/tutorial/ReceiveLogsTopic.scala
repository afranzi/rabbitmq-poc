package com.afranzi.data.rabbit.tutorial

import com.afranzi.data.rabbit.Utils._
import com.rabbitmq.client.{Channel, Connection, DeliverCallback, Delivery}

object ReceiveLogsTopic extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-five-java.html
    */

  private val ExchangeName = "topic_logs"

  def consumeQueue(queueName: String, bindingKey: String*)(implicit channel: Channel): Unit = {
    val deliverCallback: DeliverCallback = new DeliverCallback {
      def handle(consumerTag: String, delivery: Delivery): Unit = {
        val message = new String(delivery.getBody, "UTF-8")
        val routingKey = delivery.getEnvelope.getRoutingKey
        val deliveryTag = delivery.getEnvelope.getDeliveryTag

        println(s" [x] [$queueName][$routingKey] - Received '$message'")
        channel.basicAck(deliveryTag, false)
      }
    }

    bindingKey.foreach(key => channel.queueBind(queueName, ExchangeName, key))
    channel.basicConsume(queueName, false, deliverCallback, genericCancelCallback)
  }

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    implicit val channel: Channel = connection.createChannel()
    channel.exchangeDeclare(ExchangeName, "topic")
    channel.basicQos(1)

    val queueName = channel.queueDeclare().getQueue
    consumeQueue(queueName, "lazy.#", "*.red.*", "#.fox")

    val queueName2 = channel.queueDeclare().getQueue
    consumeQueue(queueName2, "quick.#", "*.red.*", "#.rabbit")
  }

  run()
}
