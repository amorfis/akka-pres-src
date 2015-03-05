package pl.szjug.akka.c4.failingactor

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job

object RunBrokenActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  private val imageSize = Size2i(2000, 1500)
  private val quality = 300

  val master = system.actorOf(Props(new BrokenActorsMaster(imageSize)))

  logger.info("Starting!")

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
