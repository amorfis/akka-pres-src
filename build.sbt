name := "akka-pres"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9",
  "com.typesafe.akka" %% "akka-remote" % "2.3.9",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.1",
  "joda-time" % "joda-time" % "2.7"
)
