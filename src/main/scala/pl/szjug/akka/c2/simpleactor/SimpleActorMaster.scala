package pl.szjug.akka.c2.simpleactor

import akka.actor.ActorRef
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.PaintingResultsActor
import pl.szjug.fractals.Job

case class SimpleActorMaster(imgSize: Size2i, worker: ActorRef) extends PaintingResultsActor {

  val handleJob: Receive = {
    case j: Job => worker ! j
  }

  override val receive = handleJob orElse paintResultPixels(imgSize)

}
