package pl.szjug.akka.c3_1.remotemanyactors

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.fractals.Job

object RunRemoteActors extends App with LazyLogging {

  val config = ConfigFactory.load("remote-many-application-host.conf")
  val system = ActorSystem("remoteActorSystem", config)
  val imageSize = Size2i(1000, 800)

  val remoteHost = ConfigFactory.load("application-virtual.conf").getString("akka.remote.netty.tcp.hostname")

  val master = system.actorOf(Props(new RemoteActorsMaster(imageSize, remoteHost)), "master")

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
