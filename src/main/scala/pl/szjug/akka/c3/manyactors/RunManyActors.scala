package pl.szjug.akka.c3.manyactors

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.akka.Constants._

object RunManyActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val master = system.actorOf(Props(new ManyActorsMaster(imageSize, classOf[ActorRenderer])), "master")

  logger.info("Starting!")

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
