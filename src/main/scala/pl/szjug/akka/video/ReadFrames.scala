package pl.szjug.akka.video

import com.xuggle.mediatool.event.IVideoPictureEvent
import com.xuggle.mediatool.{MediaListenerAdapter, ToolFactory}
import com.xuggle.xuggler.{IError, Utils}
import com.xuggle.xuggler.video.{IConverter, ConverterFactory}

object ReadFrames {

  def apply(videoUrl: String, listener: Listener) = {
    val reader = ToolFactory.makeReader(videoUrl)
    var converter: IConverter = null

    /** Register a listener that will forward all events down the Reactive Streams chain. */
    reader.addListener(new MediaListenerAdapter() {
      override def onVideoPicture(e: IVideoPictureEvent): Unit = {
        if (e.getMediaData.isComplete) {
          if (converter == null) converter = ConverterFactory.createConverter("XUGGLER-BGR-24", e.getMediaData)

          val frame = Frame(converter.toImage(e.getMediaData), e.getTimeStamp, e.getTimeUnit)
          listener.onFrame(frame)
        }
      }
    })

    var error: IError = null
    while(error == null) {
      error = reader.readPacket()
    }

    listener.onFramesEnd()
  }
}

trait Listener {
  def onFramesEnd()
  def onFrame(frame: Frame)
}
