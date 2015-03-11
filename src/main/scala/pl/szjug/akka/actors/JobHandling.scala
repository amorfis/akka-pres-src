package pl.szjug.akka.actors

import akka.actor.{Actor, ActorRef}
import com.mkrcah.fractals.{Point2i, Region2i, Size2i}
import pl.szjug.fractals.Job

trait JobHandling {
  extendee: Actor =>

  val workers: Seq[ActorRef]

  val handleJob: Receive = {
    case Job(size, _, palette, quality) =>
      val regions = divideIntoParts(size, 1, workers.size)
      for (i <- 0 to regions.size - 1) {
        workers(i).tell(Job(size, regions(i), palette, quality), self)
      }
  }

  def divideIntoParts(size: Size2i, rowsCount: Int, columnsCount: Int) = {
    val colWidth = size.width / columnsCount
    val remainingWidth = size.width % columnsCount
    val rowHeight = size.height / rowsCount
    val remainingHeight = size.height % rowsCount

    val lastColumn = columnsCount - 1
    val lastRow = rowsCount - 1

    for (col <- 0 to lastColumn;
         row <- 0 to lastRow) yield {
      val tl = Point2i(col * colWidth, row * rowHeight)
      val right = col * colWidth + colWidth + (if (col < lastColumn) 0 else remainingWidth) - 1
      val bottom = row * rowHeight + rowHeight + (if (row < lastRow) 0 else remainingHeight) - 1

      Region2i(tl, Point2i(right, bottom))
    }
  }

}
