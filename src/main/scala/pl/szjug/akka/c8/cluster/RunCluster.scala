package pl.szjug.akka.c8.cluster

import akka.actor.{PoisonPill, ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.fractals.Job

object RunCluster extends App with LazyLogging {

  val config = ConfigFactory.parseString("akka.cluster.roles=[worker]")
    .withFallback(ConfigFactory.load("cluster-application.conf"))
  val system = ActorSystem("ClusterSystem", config)

  val worker = system.actorOf(Props[ActorRenderer], "worker")
}

