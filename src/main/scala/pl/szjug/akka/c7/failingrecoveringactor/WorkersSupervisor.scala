package pl.szjug.akka.c7.failingrecoveringactor

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{OneForOneStrategy, Props, Actor}
import pl.szjug.akka.actors.{RendererException, BrokenActorRenderer}

import scala.util.Random
import scala.concurrent.duration._

class WorkersSupervisor extends Actor {

  val workers = for (i <- 1 to 20) yield context.actorOf(Props[BrokenActorRenderer])

  override def receive = {
    case message: Any => workers(Random.nextInt(workers.size)) forward message
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case RendererException(j, s) =>
        (workers(Random.nextInt(workers.size)) ! j)(s)
        Restart
      case t => super.supervisorStrategy.decider.apply(t)
    }

}