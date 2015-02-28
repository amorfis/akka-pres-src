package pl.szjug.akka

import akka.actor.{Props, ActorSystem}
import akka.routing.RoundRobinGroup
import com.typesafe.config.{ConfigFactory, Config}
import pl.szjug.akka.wife.Protocol.RequestPermission
import pl.szjug.akka.wife.Wife

object ActorsApp extends App {

  val config = ConfigFactory.load("application-host.conf")

  val system = ActorSystem("sys", config)

  val remoteWife = system.actorOf(Props[Wife], name = "wife")

  remoteWife ! RequestPermission("Beer with friends?")
}
