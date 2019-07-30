package com.afranzi.data.rabbit.tutorial

import com.rabbitmq.client.{Connection, ConnectionFactory}

object RabbitMQClient {

  def connection(): Connection = {
    val factory = new ConnectionFactory()
    factory.setHost("localhost")
    factory.setUsername("rabbit")
    factory.setPassword("rabbit")
    factory.newConnection()
  }

}
