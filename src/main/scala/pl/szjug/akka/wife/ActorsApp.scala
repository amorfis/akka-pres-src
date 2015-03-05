package pl.szjug.akka.wife

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import pl.szjug.akka.wife.Protocol.RequestPermission

object ActorsApp extends App {

  val config = ConfigFactory.load("application-host.conf")

  val system = ActorSystem("sys", config)

  val remoteWife = system.actorOf(Props[Wife], name = "wife")

  remoteWife ! RequestPermission("Beer with friends?")
}
