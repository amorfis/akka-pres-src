package pl.szjug.akka.c2.simpleactor


import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.fractals.Job

object RunActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")
  val master = system.actorOf(Props[SimpleActorMaster])

  private val imageSize = Size2i(3000, 2000)
  private val quality = 300

  logger.info("Starting!")

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
