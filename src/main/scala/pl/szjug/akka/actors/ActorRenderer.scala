package pl.szjug.akka.actors

import akka.actor.{Actor, ActorLogging}
import pl.szjug.fractals.{Job, JuliaRenderer}

class ActorRenderer extends Actor with ActorLogging {


  @throws[Exception](classOf[Exception])
  override def preStart() = {
    super.preStart()
    log.debug("Renderer actor started")
  }

  override def receive = {
    case j: Job =>
      log.debug("Renderer actor received job")
      val renderer = new JuliaRenderer(j, j.imgRegion)
      // Actor is blocked here
      val pixels = renderer.render()
      sender ! pixels
      log.debug("Renderer actor sent work done")
  }

  @throws[Exception](classOf[Exception])
  override def postStop() = {
    super.postStop()
    log.debug("Renderer actor stopped")
  }
}

