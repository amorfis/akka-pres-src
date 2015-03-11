package pl.szjug.akka.actors

import akka.actor.{Actor, ActorLogging}
import pl.szjug.fractals.{Job, JuliaRenderer}

class ActorRenderer extends Actor with ActorLogging {


  override def preStart() = {
    super.preStart()
    log.debug("Starting ActorRenderer")
  }

  override def receive = {
    case j: Job =>
      log.debug("Job received")
      val renderer = new JuliaRenderer(j, j.imgRegion)
      // Actor is blocked here
      val pixels = renderer.render()
      log.debug("Sending pixels")
      sender ! pixels
      log.debug("Pixels sent")
  }
}

