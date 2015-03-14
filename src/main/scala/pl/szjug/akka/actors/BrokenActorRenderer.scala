package pl.szjug.akka.actors

import pl.szjug.fractals.{Job, JuliaRenderer}

import scala.util.Random

class BrokenActorRenderer extends ActorRenderer {

  override def receive = {
    case j: Job =>
      if (Random.nextInt(4) == 0) {
        throw new RendererException(j)
      }
      super.receive(j)
  }
}

case class RendererException(j: Job) extends RuntimeException