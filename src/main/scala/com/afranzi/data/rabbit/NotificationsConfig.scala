package com.afranzi.data.rabbit

import com.rabbitmq.client.{BuiltinExchangeType, Channel}

import scala.collection.JavaConversions._

trait NotificationsConfig {

  val NotificationsWithDelay = "notifications-with-delay"
  val NotificationsReady = "notifications-ready"
  val NotificationsToSend = "notifications-to-send"
  val NotificationsExchangeDelay = "notifications-exchange-delayed"

  val deadLetterProps: Map[String, AnyRef] = Map(
    "x-dead-letter-exchange" -> NotificationsReady,
    "x-dead-letter-routing-key" -> "delayed"
  )

  val delayedProps: Map[String, AnyRef] = Map(
    "x-delayed-type" -> BuiltinExchangeType.TOPIC.getType
  )

  def declareQueues(implicit channel: Channel): Unit = {
    channel.queueDeclare(NotificationsWithDelay, true, false, false, deadLetterProps)

    channel.exchangeDeclare(NotificationsReady, BuiltinExchangeType.TOPIC, true, false, null)
    channel.exchangeDeclare(NotificationsExchangeDelay, "x-delayed-message", true, false, delayedProps)

    channel.queueDeclare(NotificationsToSend, true, false, false, null)
  }

}
