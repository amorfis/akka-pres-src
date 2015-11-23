package pl.szjug.akka.c9.clusterwithminroles

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.util.NetworkUtil

object RunClusterNodeWithMin extends App with LazyLogging with NetworkUtil {

  val localhost = getIpAddress()
  val config = ConfigFactory.parseString(
    s"""
      |akka.remote.netty.tcp.hostname=$localhost
    """.stripMargin)
    .withFallback(ConfigFactory.load("cluster-application-min-roles.conf"))
  val system = ActorSystem("ClusterSystem", config)
}

