package pl.szjug.akka.fractals

import akka.actor.{Actor, ActorLogging}

class ActorRenderer extends Actor with ActorLogging {

  override def receive = {
    case j: Job =>
      val renderer = new JuliaRenderer(j, j.imgRegion)
      // Actor is blocked here
      val pixels = renderer.render()
      sender ! pixels
  }
}

