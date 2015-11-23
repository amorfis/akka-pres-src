package pl.szjug.akka.c10.routers

import akka.actor.{ActorSystem, Props}
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.fractals.JobToDivide

object RunRouter extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val master = system.actorOf(Props[RoutersActorsMaster], "master")

  master ! JobToDivide(ImageSize, 100, 200, Palette)
}
