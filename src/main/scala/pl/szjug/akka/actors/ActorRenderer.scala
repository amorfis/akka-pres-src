package pl.szjug.akka.actors

import akka.actor.{Actor, ActorLogging}
import pl.szjug.fractals.{Job, JuliaRenderer}

class ActorRenderer extends Actor with ActorLogging {

  override def receive = {
    case j: Job =>
      val renderer = new JuliaRenderer(j, j.imgRegion)
      // Actor is blocked here
      val pixels = renderer.render()
      sender ! pixels
  }
}

