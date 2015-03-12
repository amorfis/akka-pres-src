package pl.szjug.akka.c7.cluster

import akka.actor.{PoisonPill, ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.actors.ActorRenderer

object RunWorkerInCluster extends App with LazyLogging {

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0")
    .withFallback(ConfigFactory.parseString("akka.cluster.roles=[worker]"))
    .withFallback(ConfigFactory.load("cluster-application.conf"))
  val system = ActorSystem("ClusterSystem", config)
  val imageSize = Size2i(1000, 800)

  val worker = system.actorOf(Props[ActorRenderer], "worker")

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run = {
      println("Shutting down the actor")
      worker ! PoisonPill
      Thread.sleep(5000L)
    }
  })
}

