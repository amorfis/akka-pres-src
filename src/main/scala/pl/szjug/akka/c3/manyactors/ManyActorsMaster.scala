package pl.szjug.akka.c3.manyactors

import akka.actor.ActorRef
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}

class ManyActorsMaster(val imgSize: Size2i, override val workers: Seq[ActorRef]) extends PaintingResultsActor(imgSize) with JobHandling {

  override def receive = handleJob orElse paintResultPixels

}
