package pl.szjug.akka.c3_1.remotemanyactors

import akka.actor.{ActorSystem, AddressFromURIString, Deploy, Props}
import akka.remote.RemoteScope
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.fractals.{JobToDivide, Job}

object RunRemoteActors extends App with LazyLogging {

  val config = ConfigFactory.load("remote-many-application-host.conf")
  val system = ActorSystem("remoteActorSystem", config)
  val remoteHost = ConfigFactory.load("remote-on-virtual.conf").getString("remote.netty.tcp.hostname")


  val workers = for (i <- 1 to 4) yield {
    val addressString = s"akka.tcp://remoteActorSystem@$remoteHost:2552"
    val address = AddressFromURIString(addressString)
    val worker = system.actorOf(Props[ActorRenderer].withDeploy(Deploy(scope = RemoteScope(address))), s"remote$i")
    logger.info(s"Created remote actor ${worker.path}")
    system.actorSelection(worker.path)
  }

  val imageSize = Size2i(600, 400)
  val master = system.actorOf(Props(classOf[RemoteActorsMaster], imageSize, workers), "master")

  master ! JobToDivide(imageSize, 10, 20, HuePalette, quality)
}