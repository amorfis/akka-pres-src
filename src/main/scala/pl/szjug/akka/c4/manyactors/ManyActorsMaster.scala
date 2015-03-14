package pl.szjug.akka.c4.manyactors

import akka.actor.{ActorRef, ActorSelection}
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}

case class ManyActorsMaster(imgSize: Size2i, workers: Seq[ActorRef])
  extends PaintingResultsActor with JobHandling {

  override val receive = handleJob(workers) orElse paintResultPixels(imgSize)

}
