package pl.szjug.akka.c5.failingrecoveringactor

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{OneForOneStrategy, Props}
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor, BrokenActorRenderer, RendererException}

import scala.concurrent.duration._
import scala.util.Random

class RecoveringActorsMaster(imgSize: Size2i) extends PaintingResultsActor(imgSize) with JobHandling {

  override val workers = for (i <- 1 to 100) yield context.actorOf(Props[BrokenActorRenderer])

  override val receive = handleJob orElse paintResultPixels

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case r: RendererException =>
        workers(Random.nextInt(workers.size)) ! r.j
        Restart
      case t => super.supervisorStrategy.decider.apply(t)
    }
}


