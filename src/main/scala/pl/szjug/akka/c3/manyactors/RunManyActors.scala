package pl.szjug.akka.c3.manyactors

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job

object RunManyActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  private val imageSize = Size2i(2000, 1500)
  private val quality = 300

  val master = system.actorOf(Props(new ManyActorsMaster(imageSize)))

  logger.info("Starting!")

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
