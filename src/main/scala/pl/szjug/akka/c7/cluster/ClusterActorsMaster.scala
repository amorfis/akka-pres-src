package pl.szjug.akka.c7.cluster

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}
import pl.szjug.fractals.{Result, Job}

import scala.util.Random

class ClusterActorsMaster extends PaintingResultsActor with JobHandling {

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

  var newWorkerAdded: () => Unit = { () =>
    if (workers.size >= MinWorkers) {
      log.info("Has enough workers. Accepting job")
      context become (handleClusterMembers orElse acceptJob)
      newWorkerAdded = () => {}
    }
  }

  def randomWorker = workers.toSeq(Random.nextInt(workers.size))

  val acceptJob: Receive = {
    case Job(size, region, pal, q) =>
      log.info("Accepting the job. Om nom nom....")
      val regions = divideIntoParts(size, 300, 100)
      jobsForWorkers = regions.map(Job(size, _, pal, q))
      sendJobsToWorkers
      context become (handleClusterMembers orElse acceptJob orElse paintResultPixels(size))
  }

  def wantedJobsSent = workers.size * 2

  def sendJobsToWorkers = {
    val jobsToSend = wantedJobsSent - jobsSentToWorkers
    val (forCurrentWorkers, forRestWorkers) = jobsForWorkers.splitAt(jobsToSend)

    forCurrentWorkers.foreach(randomWorker ! _)

    jobsForWorkers = forRestWorkers
    jobsSentToWorkers = jobsSentToWorkers + jobsToSend
  }

  override def paintResultPixels(imgSize: Size2i): Receive = {
    val superPaintResult = super.paintResultPixels(imgSize)

    {
      case r: Result =>
        jobsSentToWorkers -= 1
        superPaintResult(r)
        sendJobsToWorkers
    }
  }

  def actorSelection(address: Address) = {
    context.actorSelection(RootActorPath(address) / "user" / "worker")
  }

  val handleClusterMembers: Receive = {
    case MemberUp(member) if member.hasRole("worker") =>
      log.info("Worker added to cluster")
      workers = workers + actorSelection(member.address)
      newWorkerAdded()
    case UnreachableMember(member) if member.hasRole("worker") =>
      log.info("Worker detected as unreachable: {}", member)
      workers = workers - actorSelection(member.address)
    case MemberRemoved(member, previousStatus) if member.hasRole("worker") =>
      log.info("Worker is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => // ignore
  }

  val receive = handleClusterMembers
}
