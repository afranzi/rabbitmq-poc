package com.afranzi.data.rabbit.tutorial

import com.afranzi.data.rabbit.Utils._
import com.rabbitmq.client.Connection

import scala.util.Random

object EmitLogTopic extends App {

  /**
    * https://www.rabbitmq.com/tutorials/tutorial-five-java.html
    */

  val speeds: Seq[String] = Seq("lazy", "quick")
  val colors: Seq[String] = Seq("orange", "pink", "red", "white")
  val species: Seq[String] = Seq("rabbit", "fox", "turtle")

  implicit val random: Random = new Random()
  val messagesToSend = 100

  private val exchangeName = "topic_logs"

  def run(): Unit = {
    val connection: Connection = RabbitMQClient.connection()
    val channel = connection.createChannel()
    channel.exchangeDeclare(exchangeName, "topic")

    var counter = 1
    messagesToSend.times {
      val speed = speeds.randomItem
      val color = colors.randomItem
      val specie = species.randomItem
      val routingKey = s"$speed.$color.$specie"
      val message = s"$counter"

      channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"))
      println(s" [x] Sent '$routingKey':'$message'")
      Thread.sleep(100)
      counter += 1
    }

    channel.close()
    connection.close()
  }

  run()

}
