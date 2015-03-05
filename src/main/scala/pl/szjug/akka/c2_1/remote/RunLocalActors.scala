package pl.szjug.akka.c2_1.remote

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.fractals.Job

object RunLocalActors extends App with LazyLogging {

  val config = ConfigFactory.load("application-host.conf")
  val system = ActorSystem("actorSystem", config)

  val imageSize = Size2i(80, 40)

  logger.info("Created local ActorSystem. Connecting to remote actors.")
  val remoteRenderer = system.actorSelection("akka.tcp://actorSystem@192.168.50.4:2552/user/remoteRenderer")
  val master = system.actorOf(Props(new SimpleRemoteActorMaster(imageSize, remoteRenderer)))

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
