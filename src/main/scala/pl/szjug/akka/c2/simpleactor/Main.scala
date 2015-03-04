package pl.szjug.akka.c2.simpleactor

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import akka.actor.{ActorSystem, Props}
import com.mkrcah.fractals._
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.c1.onethreaded.Main._
import pl.szjug.akka.fractals.JuliaRenderer

object Main extends App with LazyLogging {

  val system = ActorSystem("actorSystem")
  val master = system.actorOf(Props[ActorMaster])

  private val imageSize = Size2i(1000, 700)
  private val quality = 300

  logger.info("Starting!")

  val renderer = new JuliaRenderer(imageSize, HuePalette, quality, new Region2i(imageSize))

  master ! renderer
}
