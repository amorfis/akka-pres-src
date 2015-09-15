package pl.szjug.akka.c4.manyactors

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import kamon.Kamon
import pl.szjug.akka.Constants._
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.fractals.JobToDivide

object RunManyActors extends App with LazyLogging {
  Kamon.start()

  private val size = Size2i(5000, 5000)
  val system = ActorSystem("actorSystem")

  val workers = for (i <- 1 to 8) yield system.actorOf(Props[ActorRenderer])

  val master = system.actorOf(Props(classOf[ManyActorsMaster], size, workers), "master")

  master ! JobToDivide(size, 100, 100, palette)
}
