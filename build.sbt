name := "akka-pres"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-remote" % "2.3.9",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.1",
  "joda-time" % "joda-time" % "2.7",
  "org.slf4j" % "slf4j-api" % "1.7.6",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "ch.qos.logback" % "logback-core" % "1.1.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.typesafe.akka" %% "akka-cluster" % "2.3.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.9",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
