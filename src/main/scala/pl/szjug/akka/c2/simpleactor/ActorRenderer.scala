package pl.szjug.akka.c2.simpleactor

import akka.actor.{Actor, ActorLogging}
import pl.szjug.akka.fractals.JuliaRenderer
import pl.szjug.akka.wife.Protocol.RequestPermission

class ActorRenderer extends Actor with ActorLogging {

  override def receive = {
    case r: JuliaRenderer => {
      // Actor is blocked here
      val pixels = r.render()
      sender ! pixels
    }
  }
}

