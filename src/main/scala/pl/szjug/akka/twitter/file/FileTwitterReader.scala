package pl.szjug.akka.twitter.file

import java.io._

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import twitter4j.Status

class FileTwitterReader(val file: File, val tweetHandler: Status => Unit) {

  def read(): Unit = {
    val in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))

    try {
      Stream.continually(in.readObject().asInstanceOf[Status]).takeWhile(_ != null).foreach(tweetHandler)
    } finally {
      in.close()
    }
  }
}
