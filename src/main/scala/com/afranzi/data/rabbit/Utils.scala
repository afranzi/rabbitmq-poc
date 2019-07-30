package com.afranzi.data.rabbit

import com.rabbitmq.client.{CancelCallback, LongString}

import scala.util.Random

object Utils {

  implicit class Rep(n: Int) {
    def times[A](f: => A) {
      1 to n foreach (_ => f)
    }
  }

  implicit class RandomSeq[T](s: Seq[T]) {
    def randomItem(implicit random: Random): T = s(random.nextInt(s.length))
  }

  def getHeader(key: String, headers: Map[String, AnyRef]): String = {
    val expirationTimeBytes: LongString = headers(key).asInstanceOf[LongString]
    new String(expirationTimeBytes.getBytes, "UTF-8")
  }

  val genericCancelCallback: CancelCallback = new CancelCallback {
    override def handle(consumerTag: String): Unit = {
      println(s" [x] Canceled '$consumerTag'")
    }
  }
}
