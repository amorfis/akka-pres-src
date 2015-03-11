package pl.szjug.akka.c7.cluster

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.c3.manyactors.ManyActorsMaster

class SimpleClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    //#subscribe
    cluster.subscribe(
      self,
      initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent],
      classOf[UnreachableMember])
    //#subscribe
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}

class ClusterActorsMaster(imgSize: Size2i) extends ManyActorsMaster(imgSize, null) {

//  override val Rows = 20
//  override val Columns = 40
//
//  override val workers = for(i <- 1 to Rows * Columns) yield {
//    val worker = context.actorOf(Props[ActorRenderer], s"remote$i")
//    log.info(s"Created remote actor ${worker.path}")
//    worker
//  }

//  override val handleJob: Receive = {
//    case Job(size, _, palette, quality) =>
//      val regions = divideIntoParts(size, Rows, Columns)
//      for (i <- 0 to regions.size - 1) {
//        context.actorOf(Props[ActorRenderer])
//        val job = Job(size, regions(i), palette, quality)
//        workers(0) ! job
//      }
//  }
}
