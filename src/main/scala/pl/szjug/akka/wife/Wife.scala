package pl.szjug.akka.wife

import akka.actor.{Actor, ActorLogging}
import pl.szjug.akka.wife.Protocol.RequestPermission

class Wife extends Actor with ActorLogging {

  override def receive = {
    case RequestPermission(event) => log.info(s"Husband requested $event")
  }
}

object Protocol {
  case class RequestPermission(event: String)
}