package pl.szjug.akka.c3_1.remotemanyactors

import akka.actor._
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.c3.manyactors.ManyActorsMaster
import pl.szjug.fractals.Job

import scala.util.Random

class RemoteActorsMaster(imgSize: Size2i, workers: Seq[ActorSelection], rows: Int, cols: Int) extends ManyActorsMaster(imgSize, workers) {

  override def handleJob(workers: Seq[ActorSelection]): Receive = {
    case Job(size, _, palette, quality) =>
      val regions = divideIntoParts(size, rows, cols)
      for (i <- 0 to regions.size - 1) {
        val job = Job(size, regions(i), palette, quality)
        workers(Random.nextInt(workers.size)) ! job
      }
  }
}
