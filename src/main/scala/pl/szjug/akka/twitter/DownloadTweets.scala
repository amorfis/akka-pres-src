package pl.szjug.akka.twitter

import java.io.File
import java.util.concurrent.ExecutorService

import akka.actor.ActorSystem
import org.joda.time.DateTime
import pl.szjug.akka.twitter.file.FileTwitterWriter

object DownloadTweets extends App {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val system = ActorSystem("tweetssys")

  system.scheduler.schedule(50 milliseconds, 16 minutes, new Runnable {
    override def run() = {
      println("Starting scheduled download")

      val now = DateTime.now().toString("yyyyMMddHHmmss")

      new TwitterGatherer().getManyTweets(15000, "scala", "en", new FileTwitterWriter(new File(s"tweets/tweets$now"), append = true))

      println(s"Finished scheduled download to file tweets$now}")
    }
  })
}
