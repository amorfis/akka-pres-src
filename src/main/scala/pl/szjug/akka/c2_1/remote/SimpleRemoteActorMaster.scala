package pl.szjug.akka.c2_1.remote

import akka.actor.ActorSelection
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.MasterActor
import pl.szjug.fractals.Job

class SimpleRemoteActorMaster(imgSize: Size2i, worker: ActorSelection) extends MasterActor(imgSize) {

  override def receive = handleJob orElse paintResultPixels

  val handleJob: Receive = {
    case j: Job => worker ! j
  }
}
