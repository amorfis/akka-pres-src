package pl.szjug.akka.twitter.file

import java.io._

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import pl.szjug.akka.twitter.TweetsWriter
import twitter4j.Status

class FileTwitterWriter(val file: File, val append: Boolean) extends TweetsWriter {

  val out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file, append)))

  var count = 1

  override def write(tweets: Seq[Status]) = {
    for (tweet <- tweets) {
      out.writeObject(tweet)
    }
    println(s"Batch $count written")
    count += 1
  }

  override def close = {
    out.close()
  }
}
