package pl.szjug.akka.c8.cluster

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.util.NetworkUtil

object RunWorkerInCluster extends App with LazyLogging with NetworkUtil {

  val localhost = getIpAddress()
  val config = ConfigFactory.parseString(
    s"""
       |akka.cluster.roles=[worker]
       |akka.remote.netty.tcp.hostname=$localhost
       |akka.remote.netty.tcp.port=0
    """.stripMargin)
    .withFallback(ConfigFactory.load("cluster-application.conf"))
  val system = ActorSystem("ClusterSystem", config)
  val imageSize = Size2i(1000, 800)

  val worker = system.actorOf(Props[ActorRenderer], "worker")
}

