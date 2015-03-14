package pl.szjug.akka.c7.failingrecoveringactor

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{ActorRef, OneForOneStrategy, Props}
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor, BrokenActorRenderer, RendererException}

import scala.concurrent.duration._
import scala.util.Random

class RecoveringActorsMaster(imgSize: Size2i) extends PaintingResultsActor with JobHandling {

  val workers = for (i <- 1 to 100) yield {
    val actorRef = context.actorOf(Props[BrokenActorRenderer])
    context.actorSelection(actorRef.path)
  }

  override val receive = handleJob(workers) orElse paintResultPixels(imgSize)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case r: RendererException =>
        workers(Random.nextInt(workers.size)) ! r.j
        Restart
      case t => super.supervisorStrategy.decider.apply(t)
    }
}


