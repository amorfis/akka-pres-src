package pl.szjug.akka.c2.simpleactor

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import akka.actor.{ActorLogging, Props, Actor}
import pl.szjug.akka.fractals.{Result, JuliaRenderer}

class ActorMaster extends Actor with ActorLogging {

  val worker = context.actorOf(Props[ActorRenderer])

  override def receive = {
    case r: JuliaRenderer => {
      worker ! r
    }
    case result: Result => {

      val img = new BufferedImage(result.imgSize.width, result.imgSize.height, BufferedImage.TYPE_INT_RGB)
      for ((pixel, color) <- result.pixels) {
        img.setRGB(pixel.x, pixel.y, color.toRGB.toInt)
      }

      log.info("Writing to file")
      ImageIO.write(img, "png", new File(s"julia${result.renderQuality}${result.imgSize}.png"))

      context.system.shutdown()
    }
  }
}
