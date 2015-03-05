package pl.szjug.akka.c2.simpleactor

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import akka.actor.{ActorLogging, Props, Actor}
import pl.szjug.akka.fractals.{ActorRenderer, Job, Result}

class SimpleActorMaster extends Actor with ActorLogging {

  val worker = context.actorOf(Props[ActorRenderer])

  override def receive = {
    case j: Job => worker ! j

    case result: Result =>
      val img = new BufferedImage(result.imgSize.width, result.imgSize.height, BufferedImage.TYPE_INT_RGB)
      for ((pixel, color) <- result.pixels) {
        img.setRGB(pixel.x, pixel.y, color.toRGB.toInt)
      }

      val filename = s"julia${result.renderQuality}${result.imgSize}.png"
      log.info(s"Writing to file $filename")
      ImageIO.write(img, "png", new File(filename))
      log.info(s"File $filename written")

      context.system.shutdown()
  }
}
