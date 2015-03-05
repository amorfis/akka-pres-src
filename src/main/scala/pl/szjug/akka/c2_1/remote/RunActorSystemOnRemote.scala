package pl.szjug.akka.c2_1.remote

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import pl.szjug.akka.actors.ActorRenderer

object RunActorSystemOnRemote extends App {

  val config = ConfigFactory.load("application-virtual.conf")
  val system = ActorSystem("actorSystem", config)

  system.actorOf(Props[ActorRenderer], "remoteRenderer")

}
