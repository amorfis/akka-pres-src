package pl.szjug.akka.c5.failingrecoveringactor

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{OneForOneStrategy, Props}
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{BrokenActorRenderer, RendererException}
import pl.szjug.akka.c4.failingactor.BrokenActorsMaster

import scala.concurrent.duration._
import scala.util.Random

class RecoveringActorsMaster(imgSize: Size2i) extends BrokenActorsMaster(imgSize) {

  val actor = context.actorOf(Props[BrokenActorRenderer])
  override val workers = for (i <- 1 to Rows * Columns) yield actor

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case r: RendererException =>
        workers(Random.nextInt(workers.size)) ! r.j
        Restart
      case t => super.supervisorStrategy.decider.apply(t)
    }
}


