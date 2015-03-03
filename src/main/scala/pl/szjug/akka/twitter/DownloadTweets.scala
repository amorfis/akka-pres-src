package pl.szjug.akka.twitter

import java.io.File
import java.nio.file.Paths

import akka.actor.ActorSystem
import org.joda.time.DateTime
import pl.szjug.akka.twitter.file.{SingleValueFileStore, FileTwitterWriter}

object DownloadTweets extends App {

  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global

  val system = ActorSystem("tweetssys")

  system.scheduler.schedule(50 milliseconds, 16 minutes, new Runnable {
    override def run() = {
      println("Starting scheduled download")
      val store = new SingleValueFileStore(Paths.get("tweets/oldestReadId"))

      val now = DateTime.now().toString("yyyyMMddHHmmss")

      val oldestReadIdFromFile = store.load()

      lazy val writer = new FileTwitterWriter(new File(s"tweets/tweets$now"), append = true)

      val oldestReadId = new TwitterGatherer().getManyTweets(
        15000,
        "scala",
        oldestReadIdFromFile.getOrElse(Long.MaxValue),
        "en",
        writer)

      println(s"Finished scheduled download to file tweets$now")
      println(s"Writing oldest read id: $oldestReadId to file")

      store.store(oldestReadId)
    }
  })
}
