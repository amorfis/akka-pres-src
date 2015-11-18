package pl.szjug.akka.c10.routers

import akka.actor.{Props, Terminated, ActorRef, ActorSelection}
import akka.routing._
import com.mkrcah.fractals.{Palette, Size2i}
import pl.szjug.akka.actors.{ActorRenderer, JobHandling, PaintingResultsActor}
import pl.szjug.fractals.{JobToDivide, JobWithId, Job}

class RoutersActorsMaster extends PaintingResultsActor with JobHandling {

  var router: Router = null

  def createRouter(count: Int) = {
    val routees = Vector.fill(count) {
      val r = context.actorOf(Props[ActorRenderer])
      context watch r
      ActorRefRoutee(r)
    }

//    router = Router(RoundRobinRoutingLogic(), routees)
//    router = Router(RandomRoutingLogic(), routees)
    router = Router(SmallestMailboxRoutingLogic(), routees)
  }

  val Rows = 15
  val Columns = 40

  override val receive: Receive = {
    def handleJob(size: Size2i, rows: Int, columns: Int, palette: Palette) = {
      val jobs = divideIntoParts(size, rows, columns) map (Job(size, _, palette))
      createRouter(Rows * Columns)
      jobs.foreach(router.route(_, self))
      context become (receive orElse paintResultPixels(size))
    }

    {
      case Job(size, _, palette) =>
        handleJob(size, Rows, Columns, palette)

      case JobToDivide(size, rows, columns, palette) =>
        handleJob(size, rows, columns, palette)

      case Terminated(a) =>
        router = router.removeRoutee(a)
        val r = context.actorOf(Props[ActorRenderer])
        context watch r
        router = router.addRoutee(r)
    }
  }
}
