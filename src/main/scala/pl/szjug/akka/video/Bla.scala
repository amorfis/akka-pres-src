package pl.szjug.akka.video

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

object Bla extends App {

  var framesBuf = Seq[Frame]()
  val framesBufSize = 500
  val framesWriter = new FramesWriter(new File("movement.mp4"))

  ReadFrames(getClass.getResource("/co2.mp4").toString, new Listener {
    override def onFrame(frame: Frame) = {
      framesBuf = framesBuf :+ frame
      if (framesBuf.size >= framesBufSize) {
        println("Detecting movement")
        val movement = MovementDetector(framesBuf)
        println("Writing")
        framesWriter.writeFrames(movement)
        println("Written")

        // Empty the buffer, but leave the last frame, it's going to be the first one to
        // detect movement from it in the next buffer.
        framesBuf = Seq[Frame](framesBuf.last)
      }
    }

    override def onFramesEnd() = {
      // Write remaining buffer
      val movement = MovementDetector(framesBuf)
      framesWriter.writeFrames(movement)
      framesWriter.closeWriter()
    }
  })
}
