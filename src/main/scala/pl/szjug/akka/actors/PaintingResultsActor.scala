package pl.szjug.akka.actors

import java.awt.image.BufferedImage

import akka.actor.{Actor, ActorLogging}
import com.mkrcah.fractals.Size2i
import pl.szjug.fractals.Result
import pl.szjug.swing.ImgFrame

abstract class PaintingResultsActor extends Actor with ActorLogging {

  def paintResultPixels(imgSize: Size2i): Receive = {
    val img = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB)
    val f = ImgFrame(img)

    {
      case result: Result =>
        for ((pixel, color) <- result.pixels) {
          f.img.setRGB(pixel.x, pixel.y, color.toRGB.toInt)
          f.repaintImagePart(pixel.x, pixel.y, 1, 1)
        }
    }
  }
}
