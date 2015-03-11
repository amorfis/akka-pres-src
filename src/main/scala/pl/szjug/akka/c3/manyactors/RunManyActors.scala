package pl.szjug.akka.c3.manyactors

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.akka.Constants._

object RunManyActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val Rows = 2
  val Columns = 4

  val workers = for (i <- 1 to Rows * Columns) yield system.actorOf(Props[ActorRenderer])

  val master = system.actorOf(Props(classOf[ManyActorsMaster], imageSize, workers), "master")

  master ! Job(imageSize, Region2i(imageSize), palette, quality)
}
