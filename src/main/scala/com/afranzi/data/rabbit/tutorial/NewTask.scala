package com.afranzi.data.rabbit.tutorial

import com.rabbitmq.client.{Connection, MessageProperties}

object NewTask extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-two-java.html
    */

  private val TaskQueueName = "task_queue"

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    val channel = connection.createChannel()
    channel.queueDeclare(TaskQueueName, true, false, false, null)

    val message = if (args.length < 1) "Hello World!..." else args.mkString(" ")

    channel.basicPublish("", TaskQueueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"))

    println(" [x] Sent '" + message + "'")
    channel.close()
    connection.close()
  }

  run()

}
