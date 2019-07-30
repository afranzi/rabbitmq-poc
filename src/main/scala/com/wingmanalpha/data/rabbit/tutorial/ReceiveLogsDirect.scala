/*
 * Copyright (c) 2019 Telefonica Innovacion Alpha. All rights reserved.
 */

package com.wingmanalpha.data.rabbit.tutorial

import com.rabbitmq.client.{CancelCallback, Connection, DeliverCallback, Delivery}

object ReceiveLogsDirect extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-four-java.html
    */

  private val ExchangeName = "direct_logs"
  val severities = Seq("info", "warning", "debug", "error")

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()

    val channel = connection.createChannel()
    channel.exchangeDeclare(ExchangeName, "direct")
    channel.basicQos(1)

    val queueName = channel.queueDeclare().getQueue

    for (severity <- severities) {
      channel.queueBind(queueName, ExchangeName, severity)
    }

    println(" [*] Waiting for messages. To exit press CTRL+C")

    val deliverCallback: DeliverCallback = new DeliverCallback {
      def handle(consumerTag: String, delivery: Delivery): Unit = {
        val message = new String(delivery.getBody, "UTF-8")
        println(s" [x] Received '${delivery.getEnvelope.getRoutingKey}':'$message'")
        channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)
      }
    }

    val cancelCallback: CancelCallback = new CancelCallback {
      override def handle(consumerTag: String): Unit = {
        println(s" [x] Canceled '$consumerTag'")
      }
    }

    channel.basicConsume(queueName, false, deliverCallback, cancelCallback)
  }

  run()
}
