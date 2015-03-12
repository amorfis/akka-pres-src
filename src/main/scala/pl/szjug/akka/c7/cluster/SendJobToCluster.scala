package pl.szjug.akka.c7.cluster

import akka.actor.{RootActorPath, ActorPath, ActorSystem}
import akka.cluster.Cluster
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job
import pl.szjug.akka.Constants._

object SendJobToCluster extends App with LazyLogging {

  val config = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0").withFallback(ConfigFactory.load("cluster-application.conf"))
  val system = ActorSystem("ClusterSystem", config)
  val imageSize = Size2i(1000, 800)

  val masterActor = system.actorSelection("akka.tcp://ClusterSystem@127.0.0.1:6666/user/master")

  masterActor ! Job(imageSize, Region2i(imageSize), palette, 300)
}

