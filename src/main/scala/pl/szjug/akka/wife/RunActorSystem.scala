package pl.szjug.akka.wife

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object RunActorSystem extends App {

  val config = ConfigFactory.load("application-virtual.conf")

  val system = ActorSystem("sys", config)

}
