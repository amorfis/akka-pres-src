package pl.szjug.akka.video

import java.awt.Color
import java.awt.image.BufferedImage

object MovementDetector {

  def apply(frames: Seq[Frame]): Seq[Frame] = {
    val pairs = frames.zip(frames.tail)
    pairs.map({ p =>
      diff(p._1, p._2)
    })
  }

  def diff(left: Frame, right: Frame): Frame = {
    val image = new BufferedImage(left.image.getWidth, left.image.getHeight, BufferedImage.TYPE_3BYTE_BGR)
    val g = image.createGraphics()
    g.drawImage(left.image, 0, 0, null)
    g.setXORMode(Color.WHITE)
    g.drawImage(right.image, 0, 0, null)
    left.copy(image = image)
  }
}
