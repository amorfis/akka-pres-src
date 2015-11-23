package pl.szjug.akka.c7.failingrecoveringactor

import akka.actor._
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.akka.c4.manyactors.ManyActorsMaster
import pl.szjug.fractals.JobToDivide

object RunRecoveringActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val workersSupervisor = system.actorOf(Props[WorkersSupervisor])
  val master = system.actorOf(Props(
    new ManyActorsMaster(ImageSize, Seq(workersSupervisor))), "master")

  master ! JobToDivide(ImageSize, 10, 20, HuePalette)
}
