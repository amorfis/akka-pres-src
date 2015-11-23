package pl.szjug.akka.c8.cluster

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}
import pl.szjug.fractals._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Random

class ClusterActorsMaster extends PaintingResultsActor with JobHandling {

  val MinWorkers = 4

  val cluster = Cluster(context.system)

  var workers: Set[ActorSelection] = Set.empty
  var idStartForJobs = 0L
  var workersJobsHandler: WorkersJobsHandler = null

  override def preStart(): Unit = {
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent])
//      classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  val receive = handleClusterMembers

  lazy val handleClusterMembers: Receive = {
    case MemberUp(member) if member.hasRole("worker") =>
      log.info(s"Worker ${member.address} added to cluster")
      workers = workers + actorSelection(member.address)
      newWorkerAdded()
//    case UnreachableMember(member) if member.hasRole("worker") =>
//      log.info("Worker detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) if member.hasRole("worker") =>
      log.info("Worker is Removed: {} after {}", member.address, previousStatus)
      workers = workers - actorSelection(member.address)
  }

  var newWorkerAdded: () => Unit = { () =>
    if (workers.size >= MinWorkers) {
      log.info("Has enough workers. Accepting job")
      context become (handleClusterMembers orElse acceptJob)
      newWorkerAdded = () => {}
    }
  }

  lazy val acceptJob: Receive = {
    case JobToDivide(size, rows, cols, pal) =>
      log.info("Accepting the job. Om nom nom....")
      val regions = divideIntoParts(size, rows, cols)

      val jobsForWorkers = regions.zipWithIndex.map({
        case (r, n) => JobWithId(idStartForJobs + n, size, r, pal)
      })
      workersJobsHandler = new WorkersJobsHandler(jobsForWorkers)
      workersJobsHandler.sendNextBatch(workers)

      idStartForJobs += jobsForWorkers.size
      context become (handleClusterMembers orElse acceptJob orElse paintResultPixels(size))
  }

  override def paintResultPixels(imgSize: Size2i): Receive = {
    val superPaintResult = super.paintResultPixels(imgSize)

    {
      case r: ResultWithId =>
        workersJobsHandler.resultReceived(r, workers)
        superPaintResult(r.toResult)
      case RetryJobIfNecessary(job) => workersJobsHandler.retryIfNecessary(job, workers)
    }
  }

  def actorSelection(address: Address) = {
    context.actorSelection(RootActorPath(address) / "user" / "worker")
  }

  class WorkersJobsHandler(private var jobs: Seq[JobWithId]) {

    var jobsSentToWorkers = 0
    var resultsReceived = Set[Long]()

    def randomWorker(workers: Set[ActorSelection]) = {
      if (workers.isEmpty) {
        log.error("Oops, no workers. Let it crash!")
        throw new RuntimeException("No workers to do my job :(")
      }
      workers.toSeq(Random.nextInt(workers.size))
    }

    def sendToRandomWorker(job: JobWithId, workers: Set[ActorSelection]) = {
      val worker = randomWorker(workers)
      log.info(s"Sending job to ${worker}")
      worker ! job
      context.system.scheduler.scheduleOnce(10 seconds, self, RetryJobIfNecessary(job))
    }

    def sendNextBatch(workers: Set[ActorSelection]): Unit = {
      val wantedJobsSent = workers.size * 20
      val jobsToSend = wantedJobsSent - jobsSentToWorkers
      val (forCurrentWorkers, forRestWorkers) = jobs.splitAt(jobsToSend)

      forCurrentWorkers.foreach(sendToRandomWorker(_, workers))

      jobs = forRestWorkers
      jobsSentToWorkers = jobsSentToWorkers + jobsToSend
    }

    def resultReceived(result: ResultWithId, workers: Set[ActorSelection]) = {
      jobsSentToWorkers -= 1
      resultsReceived += result.jobId
      sendNextBatch(workers)
    }

    def retryIfNecessary(job: JobWithId, workers: Set[ActorSelection]) = {
      if (!resultsReceived.contains(job.id)) {
        log.info(s"Retrying job ${job.id}")
        sendToRandomWorker(job, workers)
      }
    }
  }

  case class RetryJobIfNecessary(job: JobWithId)
}


