package pl.szjug.akka.c2.simpleactor

import akka.actor.Props
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{MasterActor, ActorRenderer}
import pl.szjug.fractals.Job

class SimpleActorMaster(imgSize: Size2i) extends MasterActor(imgSize) {

  val worker = context.actorOf(Props[ActorRenderer])

  override def receive = handleJob orElse paintResultPixels

  val handleJob: Receive = {
    case j: Job => worker ! j
  }
}
