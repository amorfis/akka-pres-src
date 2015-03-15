package pl.szjug.akka.c7.failingrecoveringactor

import akka.actor.ActorRef
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}

class RecoveringActorsMaster(imgSize: Size2i, workersSupervisor: ActorRef)
  extends PaintingResultsActor with JobHandling {

  override val receive = handleJob(Seq(workersSupervisor)) orElse paintResultPixels(imgSize)

}


