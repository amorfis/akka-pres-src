package pl.szjug.akka.video

import java.io.File

import com.xuggle.mediatool.{IMediaWriter, ToolFactory}
import com.xuggle.xuggler.IRational

class FramesWriter(val file: File) {

  val frameRate = IRational.make(60, 1)
  val writer: IMediaWriter = ToolFactory.makeWriter(file.getAbsolutePath)
  var streamStarted = false

  def writeFrames(frames: Seq[Frame]): Unit = {
    if (!streamStarted) {
      writer.addVideoStream(0, 0, frameRate, frames(0).image.getWidth, frames(0).image.getHeight)
      streamStarted = true
    }

    for(frame <- frames) {
      writer.encodeVideo(0, frame.image, frame.timeStamp, frame.timeUnit)
    }
  }

  def closeWriter(): Unit = {
    writer.close()
  }
}
