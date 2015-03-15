package pl.szjug.akka.c9.clusterwithminroles

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.util.NetworkUtil

object RunClusterNode extends App with LazyLogging with NetworkUtil {

  val localhost = getIpAddress()
  val config = ConfigFactory.parseString(
    s"""
      |akka.cluster.roles=[worker]
      |akka.remote.netty.tcp.hostname=$localhost
    """.stripMargin)
    .withFallback(ConfigFactory.load("cluster-application-min-roles.conf"))
  val system = ActorSystem("ClusterSystem", config)

  val worker = system.actorOf(Props[ActorRenderer], "worker")
}

