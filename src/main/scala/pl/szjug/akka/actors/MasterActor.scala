package pl.szjug.akka.actors

import java.awt.image.BufferedImage

import akka.actor.{Actor, ActorLogging}
import com.mkrcah.fractals.Size2i
import pl.szjug.fractals.Result
import pl.szjug.swing.ImgFrame

abstract class MasterActor(imgSize: Size2i) extends Actor with ActorLogging {

  val img = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB)
  val f = ImgFrame(img)

  val paintResultPixels: Receive = {
    case result: Result =>
      log.info("Result received!")
      for ((pixel, color) <- result.pixels) {
        f.img.setRGB(pixel.x, pixel.y, color.toRGB.toInt)
      }
      val reg = result.imgPart
      f.repaintImagePart(reg.tl.x, reg.tl.y, reg.width, reg.height)
  }
}
