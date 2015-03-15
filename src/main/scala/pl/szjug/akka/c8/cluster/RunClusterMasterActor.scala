package pl.szjug.akka.c8.cluster

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.util.NetworkUtil

object RunClusterMasterActor extends App with LazyLogging with NetworkUtil {

  val localhost = getIpAddress()
  val config = ConfigFactory.parseString(
    s"""
       |akka.cluster.roles=[master]
       |akka.remote.netty.tcp.hostname=$localhost
       |akka.remote.netty.tcp.port = 6666
     """.stripMargin)
    .withFallback(ConfigFactory.load("cluster-application.conf"))
  val system = ActorSystem("ClusterSystem", config)

  val actor = system.actorOf(Props(classOf[ClusterActorsMaster]), "master")
}

