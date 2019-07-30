package com.afranzi.data.rabbit.tutorial

import com.afranzi.data.rabbit.Utils._
import com.rabbitmq.client.Connection

import scala.util.Random

object EmitLogDirect extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-four-java.html
    */

  private val ExchangeName = "direct_logs"

  val severities = Seq("info", "warning", "debug", "error")
  implicit val random: Random = new Random()
  val messagesToSend = 100

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    val channel = connection.createChannel()
    channel.exchangeDeclare(ExchangeName, "direct")

    var counter = 1

    messagesToSend.times {
      val severity = severities.randomItem
      val message = s"$counter"
      channel.basicPublish(ExchangeName, severity, null, message.getBytes("UTF-8"))
      println(s" [x] Sent '$severity':'$message'")
      Thread.sleep(100)
      counter += 1
    }

    channel.close()
    connection.close()
  }

  run()
}
