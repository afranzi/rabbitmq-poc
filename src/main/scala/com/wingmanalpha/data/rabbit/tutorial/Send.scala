/*
 * Copyright (c) 2019 Telefonica Innovacion Alpha. All rights reserved.
 */

package com.wingmanalpha.data.rabbit.tutorial

import com.rabbitmq.client.{Channel, Connection}

object Send extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-one-java.html
    */

  private val QueueName = "hello"

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    val channel: Channel = connection.createChannel()
    channel.queueDeclare(QueueName, false, false, false, null)

    val message = "Hello World!"
    channel.basicPublish("", QueueName, null, message.getBytes("UTF-8"))
    println(" [x] Sent '" + message + "'")
    channel.close()
    connection.close()
  }

  run()

}
