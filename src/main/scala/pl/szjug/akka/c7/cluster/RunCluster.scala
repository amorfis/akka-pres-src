package pl.szjug.akka.c7.cluster

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.fractals.Job

object RunCluster extends App with LazyLogging {

  val config = ConfigFactory.load("cluster-application.conf")
  val system = ActorSystem("ClusterSystem", config)
  val imageSize = Size2i(1000, 800)

//  Cluster.get(system).join(Address("akka.tcp", "remoteActorSystem", "192.168.50.4", 2552))

//  system.actorOf(Props[SimpleClusterListener], name = "clusterListener")
  val master = system.actorOf(Props(new ClusterActorsMaster(imageSize)), "master")
//
  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}

