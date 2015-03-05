package pl.szjug.akka.c4.failingactor

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.actors.BrokenActorRenderer
import pl.szjug.akka.c3.manyactors.ManyActorsMaster
import pl.szjug.fractals.Job
import pl.szjug.akka.Constants._

object RunBrokenActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val master = system.actorOf(Props(new ManyActorsMaster(imageSize, classOf[BrokenActorRenderer])))

  logger.info("Starting!")

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
