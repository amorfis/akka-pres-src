package pl.szjug.akka.twitter

import java.io.{EOFException, File}

import pl.szjug.akka.twitter.file.{FileTwitterReader, FileTwitterWriter}

object ReadTweets extends App {

  var count = 0

  val files = new File("tweets").listFiles().sortBy(_.getName)

  files.foreach { file =>
    println(s"Reading file ${file.getName}")
    try {
      new FileTwitterReader(file, { status =>
//        println(s"$count ${status.getText}")
        count += 1
      }).read()
    } catch {
      case e: EOFException => //ignore
      case e: Throwable => e.printStackTrace()
    }
    println(s"$count tweets read")
  }

}
