organization := "com.afranzi.data"
name := "rabbit-mq"
description := "RabbitMQ PoCs"
homepage := Some(url("https://github.com/afranzi/rabbitmq-poc"))
scalaVersion := "2.11.8"
startYear := Some(2019)

javacOptions ++= List("-source", "1.8", "-target", "1.8")

resolvers += Resolver.mavenCentral

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.3", // https://github.com/lightbend/config
  "org.rogach" %% "scallop" % "3.1.3", // https://github.com/scallop/scallop

  //- RABBITMQ
  "com.rabbitmq" % "amqp-client" % "5.7.3", // https://www.rabbitmq.com/api-guide.html

  // - LOGGERS
  "org.clapper" %% "grizzled-slf4j" % "1.3.2" exclude("org.slf4j", "slf4j-api"), // http://software.clapper.org/grizzled-slf4j/
  "org.slf4j" % "slf4j-log4j12" % "1.7.26", // https://www.slf4j.org/

  // - TESTS
  "org.scalatest" %% "scalatest" % "3.0.5" % Test, // http://scalatest.org/
  "org.mockito" %% "mockito-scala" % "1.0.9" % Test, // https://github.com/mockito/mockito-scala
  "org.scoverage" %% "scalac-scoverage-runtime" % "1.3.1" % Provided
)
