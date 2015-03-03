package pl.szjug.akka.twitter

import java.io.{EOFException, File}

import pl.szjug.akka.twitter.file.{FileTwitterReader, FileTwitterWriter}
import twitter4j.Status

object ReadTweets extends App {

  var count = 0

  val files = new File("tweets").listFiles().toList.filter(_.getName.startsWith("tweets")).sortBy(_.getName)

  var tweetIdToFile = scala.collection.mutable.Map[Long, String]()
  var duplicates = scala.collection.mutable.Map[String, scala.collection.mutable.Set[String]]().withDefault(s => scala.collection.mutable.Set[String]())

  def handleHelperData(file: File, status: Status) {
    if (tweetIdToFile.get(status.getId).isDefined) {
      val oldFileContainingThisTweet = tweetIdToFile(status.getId)
      //          println(s"Tweet ${status.getId} is already contained in file ${tweetIdToFile(status.getId)} but also in ${file.getName}")
      val duplicatedFiles = duplicates(oldFileContainingThisTweet)
      duplicatedFiles.add(file.getName)
      duplicates(oldFileContainingThisTweet) = duplicatedFiles
    }
    tweetIdToFile.put(status.getId, file.getName)
  }

  files.foreach { file =>
    println(s"Reading file ${file.getName}")
    var idsFromThisFile = List[Long]()
    try {
      new FileTwitterReader(file, { status =>
//        println(s"$count ${status.getId}")
        count += 1

        handleHelperData(file, status)

        idsFromThisFile = idsFromThisFile :+ status.getId.toLong
      }).read()
    } catch {
      case e: EOFException => //ignore
      case e: Throwable => e.printStackTrace()
    }
    val minIdInFile = idsFromThisFile.foldLeft(Long.MaxValue) {
      case p if p._2 < p._1 => p._2
      case p => p._1
    }

    val maxIdInFile = idsFromThisFile.foldLeft(Long.MinValue) {
      case p if p._2 > p._1 => p._2
      case p => p._1
    }

    println(s"In file ${file.getName} max id is $maxIdInFile and min one $minIdInFile")
    println(s"$count tweets read")
  }

//  duplicates.foreach { p =>
//    println(s"Duplicates of ${p._1} ")
//    p._2.foreach(println)
//  }

  println(tweetIdToFile.size)
}
