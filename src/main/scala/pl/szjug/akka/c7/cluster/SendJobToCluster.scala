package pl.szjug.akka.c7.cluster

import akka.actor.{RootActorPath, ActorPath, ActorSystem}
import akka.cluster.Cluster
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job
import pl.szjug.akka.Constants._

object SendJobToCluster extends App with LazyLogging {

  val config = ConfigFactory.parseString("akka.actor.provider=akka.remote.RemoteActorRefProvider")
  val system = ActorSystem("externalSystem", config)
  val imageSize = Size2i(2000, 1600)

  val masterActor = system.actorSelection("akka.tcp://ClusterSystem@127.0.0.1:6666/user/master")

  logger.info("Sending job to cluster master")
  masterActor ! Job(imageSize, Region2i(imageSize), palette, 300)
}

