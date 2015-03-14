package pl.szjug.akka.c3.remote

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import pl.szjug.akka.actors.ActorRenderer

object RunActorSystemOnRemote extends App {

  val config = ConfigFactory.load("remote-single-application-virtual.conf")
  val system = ActorSystem("remoteActorSystem", config)

  system.actorOf(Props[ActorRenderer], "remoteRenderer")

}
