package pl.szjug.akka.c10.manyactors

import akka.actor.{ActorRef, ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.{JobToDivide, Job}
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.akka.Constants._

object RunRouter extends App with LazyLogging {

  val system = ActorSystem("actorSystem")

  val master = system.actorOf(Props[RoutersActorsMaster], "master")

  master ! JobToDivide(imageSize, 100, 200, palette)
}
