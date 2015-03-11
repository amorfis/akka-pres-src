package pl.szjug.akka.c3.manyactors

import akka.actor.ActorSelection
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}

case class ManyActorsMaster(imgSize: Size2i, workers: Seq[ActorSelection])
  extends PaintingResultsActor(imgSize) with JobHandling {

  override def receive = handleJob(workers) orElse paintResultPixels

}
