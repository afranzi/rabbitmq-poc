package com.afranzi.data.rabbit.tutorial

import com.afranzi.data.rabbit.Utils._
import com.rabbitmq.client._

object ReceiveLogs extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-three-java.html
    */

  private val ExchangeName = "logs"

  def consumeQueue(queueName: String)(implicit channel: Channel): Unit = {
    val deliverCallback: DeliverCallback = new DeliverCallback {
      def handle(consumerTag: String, delivery: Delivery): Unit = {
        val message = new String(delivery.getBody, "UTF-8")
        println(s" [x] [$queueName] - Received '" + message + "'")
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

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    implicit val channel: Channel = connection.createChannel()

    //    channel.queueDeclare(ExchangeName, true, false, false, null)
    channel.exchangeDeclare(ExchangeName, "fanout")

    5.times {
      val queueName = channel.queueDeclare().getQueue
      channel.queueBind(queueName, ExchangeName, "")
      consumeQueue(queueName)
    }
    println(" [*] Waiting for messages. To exit press CTRL+C")
  }

  run()
}
