/*
 * Copyright (c) 2019 Telefonica Innovacion Alpha. All rights reserved.
 */

package com.wingmanalpha.data.rabbit.tutorial

import com.rabbitmq.client.{CancelCallback, Connection, DeliverCallback, Delivery}

object Worker extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-two-java.html
    */

  private val TaskQueueName = "task_queue"

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    val channel = connection.createChannel()
    channel.queueDeclare(TaskQueueName, true, false, false, null)
    channel.basicQos(1)

    println(" [*] Waiting for messages. To exit press CTRL+C")

    val deliverCallback: DeliverCallback = new DeliverCallback {
      def handle(consumerTag: String, delivery: Delivery): Unit = {
        val message = new String(delivery.getBody, "UTF-8")
        println(" [x] Received '" + message + "'")
        try {
          doWork(message)
        } finally {
          println(" Done")
          channel.basicAck(delivery.getEnvelope.getDeliveryTag, false)
        }
      }
    }

    val cancelCallback: CancelCallback = new CancelCallback {
      override def handle(consumerTag: String): Unit = {
        println(s" [x] Canceled '$consumerTag'")
      }
    }

    channel.basicConsume(TaskQueueName, false, deliverCallback, cancelCallback)
  }

  private def doWork(task: String) {
    print(" [x] Processing ")

    for (ch <- task.toCharArray if ch == '.') {
      try {
        print(".")
        Thread.sleep(1000)
      } catch {
        case _ignored: InterruptedException => Thread.currentThread().interrupt()
      }
    }
  }

  run()
}
