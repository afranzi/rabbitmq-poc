/*
 * Copyright (c) 2019 Telefonica Innovacion Alpha. All rights reserved.
 */

package com.wingmanalpha.data.rabbit.tutorial

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
