package pl.szjug.akka.c7.cluster

import akka.actor.{ActorPath, ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.fractals.Job
import pl.szjug.akka.Constants._

object RunClusterMasterActor extends App with LazyLogging {

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port = 6666")
    .withFallback(ConfigFactory.parseString("akka.cluster.roles=[master]"))
    .withFallback(ConfigFactory.load("cluster-application.conf"))
  val system = ActorSystem("ClusterSystem", config)

  val actor = system.actorOf(Props(classOf[ClusterActorsMaster]), "master")
}

