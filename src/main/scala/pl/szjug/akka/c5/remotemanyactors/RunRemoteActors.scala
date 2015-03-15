package pl.szjug.akka.c5.remotemanyactors

import akka.actor.{ActorSystem, AddressFromURIString, Deploy, Props}
import akka.remote.RemoteScope
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.akka.c4.manyactors.ManyActorsMaster
import pl.szjug.fractals.JobToDivide

object RunRemoteActors extends App with LazyLogging {

  val config = ConfigFactory.load("remote-on-virtual.conf")
  val system = ActorSystem("actorSystem", config)
  val remoteHost = ConfigFactory.load("remote-on-virtual.conf").getString("remote.netty.tcp.hostname")

  val workers = for (i <- 1 to 16) yield {
    val address = AddressFromURIString(s"akka.tcp://remoteActorSystem@$remoteHost:2552")
    val worker = system.actorOf(Props[ActorRenderer].withDeploy(Deploy(scope = RemoteScope(address))), s"remote$i")
    logger.info(s"Created remote actor ${worker.path}")
    worker
  }

  val imageSize = Size2i(600, 400)
  val master = system.actorOf(Props(classOf[ManyActorsMaster], imageSize, workers), "master")

  master ! JobToDivide(imageSize, 10, 20, HuePalette)
}
