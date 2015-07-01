package pl.szjug.akka.c11.clustersharding

import akka.actor._
import akka.contrib.pattern.ShardRegion
import pl.szjug.akka.c11.clustersharding.StatefulActor.{GetWordCount, IncWordCount}

class StatefulActor(val persistenceId: String) extends Actor with ActorLogging {

  var words = Map[String, Int]()

  override def preStart = {
    log.info(s"Starting $self")
  }

  override def receive: Receive = {
    case s@IncWordCount(_, word) =>
      log.info(s"Got IncWordCount command, $self")
//      persist(s) { s =>
        val count = words.getOrElse(word, 0)
        words += (word -> (count + 1))
//      }
    case GetWordCount(_, word) => words.getOrElse(word, 0)
  }

//  override def receiveRecover = receiveCommand
}

object StatefulActor {

  sealed trait WordMessage {
    val id: String
    val word: String
  }

  case class IncWordCount(id: String, word: String) extends WordMessage

  case class GetWordCount(id: String, word: String) extends WordMessage

  val idExtractor: ShardRegion.IdExtractor = {
    case wm: WordMessage => (wm.id, wm)
  }

  val shardRegionResolver: ShardRegion.ShardResolver = {
    case wm: WordMessage => (Math.abs(wm.id.hashCode) % 2).toString
  }

}

