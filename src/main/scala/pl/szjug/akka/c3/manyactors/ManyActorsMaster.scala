package pl.szjug.akka.c3.manyactors

import java.awt.image.BufferedImage

import akka.actor.{Actor, ActorLogging, Props}
import com.mkrcah.fractals.{Point2i, Region2i, Size2i}
import pl.szjug.akka.fractals.{ActorRenderer, Job, Result}
import pl.szjug.akka.swing.ImgFrame

class ManyActorsMaster extends Actor with ActorLogging {

  val Rows = 2
  val Columns = 4

  val workers = for(i <- 1 to Rows * Columns) yield context.actorOf(Props[ActorRenderer])

  var f: ImgFrame = null

  override def receive = {
    case Job(imgSize, _, palette, quality) =>
      val img = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB)
      f = ImgFrame(img)

      val regions = divideIntoParts(imgSize, Rows, Columns)
      for(i <- 0 to regions.size - 1) {
        workers(i) ! Job(imgSize, regions(i), palette, quality)
      }

    case result: Result => {
      log.info("Result received!")
      for ((pixel, color) <- result.pixels) {
        f.img.setRGB(pixel.x, pixel.y, color.toRGB.toInt)
      }
      val reg = result.imgPart
      f.repaintImagePart(reg.tl.x, reg.tl.y, reg.width, reg.height)
//      f.repaint()
    }
  }

  def divideIntoParts(size: Size2i, rowsCount: Int, columnsCount: Int) = {
    val colWidth = size.width / columnsCount
    val remainingWidth = size.width % columnsCount
    val rowHeight = size.height / rowsCount
    val remainingHeight = size.height % rowsCount

    val lastColumn = columnsCount - 1
    val lastRow = rowsCount - 1

    for(col <- 0 to lastColumn;
        row <- 0 to lastRow) yield {
      val tl = Point2i(col * colWidth, row * rowHeight)
      val right = col * colWidth + colWidth + (if(col < lastColumn) 0 else remainingWidth) - 1
      val bottom = row * rowHeight + rowHeight + (if(row < lastRow) 0 else remainingHeight) - 1

      Region2i(tl, Point2i(right, bottom))
    }
  }
}
