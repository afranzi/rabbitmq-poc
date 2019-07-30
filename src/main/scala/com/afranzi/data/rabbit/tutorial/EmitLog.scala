package com.afranzi.data.rabbit.tutorial

import com.rabbitmq.client.Connection

object EmitLog extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-three-java.html
    */

  private val ExchangeName = "logs"

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    val channel = connection.createChannel()

    //    channel.queueDeclare(ExchangeName, true, false, false, null)
    channel.exchangeDeclare(ExchangeName, "fanout")

    val message = if (args.length < 1) "Hello World!" else args.mkString(" ")

    channel.basicPublish(ExchangeName, "", null, message.getBytes("UTF-8"))
    println(" [x] Sent '" + message + "'")
    channel.close()
    connection.close()
  }

  run()

}
