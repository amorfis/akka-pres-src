import com.typesafe.sbt.SbtAspectj._

name := "akka-pres"

version := "1.0"

scalaVersion := "2.11.6"

val akkaVersion = "2.3.11"
val kamonVersion = "0.3.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.1",
  "joda-time" % "joda-time" % "2.7",
  "org.slf4j" % "slf4j-api" % "1.7.6",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "ch.qos.logback" % "logback-core" % "1.1.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",

  "io.kamon" %% "kamon-core" % kamonVersion,
//  "io.kamon" %% "kamon-akka" % kamonVersion,
//  "io.kamon" %% "kamon-akka-remote" % kamonVersion,
  "io.kamon" %% "kamon-statsd" % kamonVersion,
  "io.kamon" %% "kamon-log-reporter" % kamonVersion,
  "io.kamon" %% "kamon-system-metrics" % kamonVersion,
  "org.aspectj" % "aspectjweaver" % "1.8.1"
)

addCommandAlias("runRemote", "run-main pl.szjug.akka.c3.remote.RunActorSystemOnRemote")
addCommandAlias("runClusterNode", "run-main pl.szjug.akka.c8.cluster.RunClusterNode")
addCommandAlias("runWorkerInCluster", "run-main pl.szjug.akka.c8.cluster.RunWorkerInCluster")
addCommandAlias("runClusterNodeWithMin", "run-main pl.szjug.akka.c9.clusterwithminroles.RunClusterNodeWithMin")

addCommandAlias("runManyActors", "run-main pl.szjug.akka.c4.manyactors.RunManyActors")

// Bring the sbt-aspectj settings into this build
aspectjSettings

// Here we are effectively adding the `-javaagent` JVM startup
// option with the location of the AspectJ Weaver provided by
// the sbt-aspectj plugin.
javaOptions <++= AspectjKeys.weaverOptions in Aspectj

// We need to ensure that the JVM is forked for the
// AspectJ Weaver to kick in properly and do it's magic.
fork in run := true
