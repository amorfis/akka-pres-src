package pl.szjug.akka.c6.failingactor

import akka.actor.{ActorSelection, ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.akka.actors.BrokenActorRenderer
import pl.szjug.akka.c4.manyactors.ManyActorsMaster
import pl.szjug.fractals.Job

object RunBrokenActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val workers = for (i <- 1 to 16) yield {
    system.actorOf(Props[BrokenActorRenderer], s"worker$i")
  }

  val master = system.actorOf(Props(classOf[ManyActorsMaster], imageSize, workers), "master")

  master ! Job(imageSize, Region2i(imageSize), palette)
}
