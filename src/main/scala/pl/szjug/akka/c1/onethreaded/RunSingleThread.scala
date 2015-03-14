package pl.szjug.akka.c1.onethreaded

import java.awt.image.BufferedImage

import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.JuliaRenderer
import pl.szjug.swing.ImgFrame
import pl.szjug.akka.Constants._

object RunSingleThread extends App with LazyLogging {

  logger.info("Starting!")
  
  val img = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB)
  val f = ImgFrame(img)

  val renderer = new JuliaRenderer(imageSize, HuePalette, quality, new Region2i(imageSize))
  val colorsForPixels = renderer.render()

  for((pixel, color) <- colorsForPixels.pixels) {
    img.setRGB(pixel.x, pixel.y, color.toRGB.toInt)
    f.repaint()
  }

}
