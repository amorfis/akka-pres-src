package pl.szjug.akka.c7.failingrecoveringactor

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.fractals.{JobToDivide, Job}

object RunRecoveringActors extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val master = system.actorOf(Props(new RecoveringActorsMaster(imageSize)), "master")

  master ! Job(imageSize, Region2i(imageSize), HuePalette)
}
