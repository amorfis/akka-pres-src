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
      log.info("Job received")
      val renderer = new JuliaRenderer(j)
      // Actor is blocked here
      val pixels = renderer.render()
      log.info("Sending pixels")
      sender ! pixels
      log.info("Pixels sent")
  }
}

