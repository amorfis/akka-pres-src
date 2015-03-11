package pl.szjug.akka.c7.cluster

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.mkrcah.fractals.{Region2i, Size2i}
import pl.szjug.akka.Constants._
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}
import pl.szjug.fractals.Job

import scala.util.Random

class ClusterActorsMaster(imgSize: Size2i) extends PaintingResultsActor(imgSize) with JobHandling {

  val cluster = Cluster(context.system)

  var workers: Seq[ActorSelection] = Seq.empty

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def randomWorker(workers: Seq[ActorSelection]) = workers(Random.nextInt(workers.size))

  def receive = startWhenHasWorkers orElse logEvents

  override def handleJob(workers: Seq[ActorSelection]): Receive = {
    case Job(size, _, pal, q) =>
      val regions = divideIntoParts(size, 1, workers.size)
      for (region <- regions) {
        randomWorker(workers) ! Job(size, region, pal, q)
      }
      context become (acceptWorkers orElse logEvents)
  }


  val logEvents: Receive = {
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => // ignore
  }

  val acceptWorkers: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
      workers = workers :+ context.actorSelection(RootActorPath(member.address))
  }

  val startWhenHasWorkers: Receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
      workers = workers :+ context.actorSelection(RootActorPath(member.address))

      if (workers.size > 3) {
        self ! Job(imageSize, Region2i(imageSize), palette, quality)
        context become handleJob(workers)
      }
  }

  private case class SendJobAndMessageRest(workers: Seq[ActorSelection], regions: Seq[Region2i])

}
