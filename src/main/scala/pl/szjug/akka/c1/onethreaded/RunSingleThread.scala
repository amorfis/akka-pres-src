package pl.szjug.akka.c1.onethreaded

import java.awt.image.BufferedImage

import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.fractals.JuliaRenderer
import pl.szjug.swing.ImgFrame

object RunSingleThread extends App with LazyLogging {

  private val imgSize = Size2i(2000, 1500)
  private val quality = 300

  logger.info("Starting!")

  val renderer = new JuliaRenderer(imgSize, HuePalette, quality, new Region2i(imgSize))
  val colorsForPixels = renderer.render()

  val img = new BufferedImage(imgSize.width, imgSize.height, BufferedImage.TYPE_INT_RGB)
  for((pixel, color) <- colorsForPixels.pixels) {
    img.setRGB(pixel.x, pixel.y, color.toRGB.toInt)
  }

  val f = ImgFrame(img)
}
