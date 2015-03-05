package pl.szjug.akka.c2.simpleactor


import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job
import pl.szjug.akka.Constants._

object RunActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val master = system.actorOf(Props(new SimpleActorMaster(imageSize)))

  logger.info("Starting!")

  master ! Job(imageSize, Region2i(imageSize), HuePalette, quality)
}
