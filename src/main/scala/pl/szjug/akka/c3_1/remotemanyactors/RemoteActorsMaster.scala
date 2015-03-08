package pl.szjug.akka.c3_1.remotemanyactors

import akka.actor._
import akka.remote.RemoteScope
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.akka.c3.manyactors.ManyActorsMaster
import pl.szjug.fractals.Job

import scala.util.Random

class RemoteActorsMaster(imgSize: Size2i, remoteHost: String, port: Int = 2552) extends ManyActorsMaster(imgSize, classOf[ActorRenderer]) {

  override val Rows = 20
  override val Columns = 40

  override val workers = for(i <- 1 to 4) yield {
    val addressString = s"akka.tcp://remoteActorSystem@$remoteHost:$port"
    val address = AddressFromURIString(addressString)
    val worker = context.actorOf(Props[ActorRenderer].withDeploy(Deploy(scope = RemoteScope(address))), s"remote$i")
    log.info(s"Created remote actor ${worker.path}")
    worker
  }

  override val handleJob: Receive = {
    case Job(size, _, palette, quality) =>
      val regions = divideIntoParts(size, Rows, Columns)
      for (i <- 0 to regions.size - 1) {
        val job = Job(size, regions(i), palette, quality)
        workers(Random.nextInt(workers.size)) ! job
      }
  }
}
