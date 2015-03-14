package pl.szjug.akka.c4.manyactors

import akka.actor.{ActorRef, ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.Job
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.akka.Constants._

object RunManyActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val workers = for (i <- 1 to 30) yield {
    val ref = system.actorOf(Props[ActorRenderer])
    system.actorSelection(ref.path)
  }

  val master = system.actorOf(Props(classOf[ManyActorsMaster], imageSize, workers), "master")

  master ! Job(imageSize, Region2i(imageSize), palette)
}
