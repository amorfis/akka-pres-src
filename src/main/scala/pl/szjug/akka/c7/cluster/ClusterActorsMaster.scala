package pl.szjug.akka.c7.cluster

import akka.actor._
import akka.cluster.{MemberStatus, Cluster}
import akka.cluster.ClusterEvent._
import com.mkrcah.fractals.{Palette, Region2i, Size2i}
import pl.szjug.akka.Constants._
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}
import pl.szjug.fractals.{Result, Job}

import scala.util.Random

class ClusterActorsMaster(imgSize: Size2i) extends PaintingResultsActor(imgSize) with JobHandling {

  val MinWorkers = 2

  val cluster = Cluster(context.system)

  var workers: Set[ActorSelection] = Set.empty
  var jobsForWorkers = Seq[Job]()
  var jobsSentToWorkers = 0

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def randomWorker = workers.toSeq(Random.nextInt(workers.size))

  def acceptJobIfHasWorkers() = {
    if (workers.size >= MinWorkers) {
      log.info("Accepting jobs")
      context become (acceptJob orElse handleClusterMembers orElse paintResultPixels)
    }
  }

  val waitForWorkers: Receive = {
    case _: Job =>
      log.info("Not accepting the job. Waiting for workers")
  }

  val acceptJob: Receive = {
    case Job(size, region, pal, q) =>
      log.info("Accepting the job. Nom nom nom....")
      val regions = divideIntoParts(size, 100, 10)
      jobsForWorkers = regions.map(Job(size, _, pal, q))
      sendJobsToWorkers
    case PartialJob(size, regions, pal, q) =>
      log.info("Sending partial job to worker")
      randomWorker ! Job(size, regions.head, pal, q)
      if (regions.size > 1) {
        self ! PartialJob(size, regions.tail, pal, q)
      }
  }

  def wantedJobsSent = workers.size * 2

  def sendJobsToWorkers = {
    val jobsToSend = wantedJobsSent - jobsSentToWorkers
    val (forCurrentWorkers, forRestWorkers) = jobsForWorkers.splitAt(jobsToSend)

    forCurrentWorkers.foreach(randomWorker ! _)

    jobsForWorkers = forRestWorkers
    jobsSentToWorkers = jobsSentToWorkers + jobsToSend
  }

  override def paintResultPixels: Receive = {
    case r: Result =>
      jobsSentToWorkers -= 1
      super.paintResultPixels(r)
      sendJobsToWorkers
  }

  def actorSelection(address: Address) = {
    context.actorSelection(RootActorPath(address) / "user" / "worker")
  }

  val handleClusterMembers: Receive = {
    case MemberUp(member) if member.hasRole("worker") =>
      workers = workers + actorSelection(member.address)
      acceptJobIfHasWorkers()
    case UnreachableMember(member) if member.hasRole("worker") =>
      log.info("Worker detected as unreachable: {}", member)
      workers = workers - actorSelection(member.address)
    case MemberRemoved(member, previousStatus) if member.hasRole("worker") =>
      log.info("Worker is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => // ignore
  }

  val receive = waitForWorkers orElse handleClusterMembers

  case class PartialJob(imgSize: Size2i, regionsToHandle: Seq[Region2i], palette: Palette, quality: Int)
}
