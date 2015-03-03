package pl.szjug.akka.video

import java.io.File

import com.xuggle.mediatool.{ToolFactory, IMediaWriter}
import com.xuggle.xuggler.IRational

object WriteFrames {

  def apply(file: File, frames: Seq[Frame]): Unit = {
    val writer: IMediaWriter = ToolFactory.makeWriter(file.getAbsolutePath)

    val frameRate = IRational.make(60, 1)

    writer.addVideoStream(0, 0, frameRate, frames(0).image.getWidth(), frames(0).image.getHeight())

    for(frame <- frames) {
      writer.encodeVideo(0, frame.image, frame.timeStamp, frame.timeUnit)
    }

    writer.close()
  }

}
