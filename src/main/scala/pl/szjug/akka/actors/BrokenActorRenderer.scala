package pl.szjug.akka.actors

import akka.actor.ActorRef
import pl.szjug.fractals.Job

import scala.util.Random

class BrokenActorRenderer extends ActorRenderer {

  override def receive = {
    case j: Job =>
      if (Random.nextInt(4) == 0) {
        throw new RendererException(j, sender())
      }
      super.receive(j)
  }
}

case class RendererException(j: Job, jobSender: ActorRef) extends RuntimeException