package com.afranzi.data.rabbit.tutorial

import com.rabbitmq.client.{CancelCallback, Connection, DeliverCallback, Delivery}

object Recv extends App {

  /**
    * Tutorial - https://www.rabbitmq.com/tutorials/tutorial-one-java.html
    */

  private val QueueName = "hello"

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    val channel = connection.createChannel()
    channel.queueDeclare(QueueName, false, false, false, null)

    println(" [*] Waiting for messages. To exit press CTRL+C")
    val deliverCallback: DeliverCallback = new DeliverCallback {
      def handle(consumerTag: String, delivery: Delivery): Unit = {
        val message = new String(delivery.getBody, "UTF-8")
        println(s" [x] Received '$message'")
      }
    }

    val cancelCallback: CancelCallback = new CancelCallback {
      override def handle(consumerTag: String): Unit = {
        println(s" [x] Canceled '$consumerTag'")
      }
    }

    channel.basicConsume(QueueName, true, deliverCallback, cancelCallback)
  }

  run()
}
