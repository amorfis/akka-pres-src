package pl.szjug.akka.c5.remotemanyactors

import akka.actor._
import com.mkrcah.fractals.Size2i
import pl.szjug.akka.c4.manyactors.ManyActorsMaster
import pl.szjug.fractals.{JobToDivide, Job}

import scala.util.Random

class RemoteActorsMaster(imgSize: Size2i, workers: Seq[ActorSelection]) extends ManyActorsMaster(imgSize, workers) {

  override def handleJob(workers: Seq[ActorSelection]): Receive = {
    case JobToDivide(size, rows, cols, palette, quality) =>
      val regions = divideIntoParts(size, rows, cols)
      for (i <- 0 to regions.size - 1) {
        val job = Job(size, regions(i), palette)
        workers(Random.nextInt(workers.size)) ! job
      }
  }
}
