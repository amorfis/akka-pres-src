package pl.szjug.akka.c12.clustersharding

import akka.actor.{ActorLogging, Actor}
import akka.cluster.sharding.ShardRegion
import akka.cluster.sharding.ShardRegion.{ExtractEntityId, HashCodeMessageExtractor, MessageExtractor}
import StatefulActor.{GetWordCount, IncWordCount}

class StatefulActor() extends Actor with ActorLogging {

  var words = Map[String, Int]()

  override def preStart = {
    log.info(s"Starting $self")
  }

  override def receive: Receive = {
    case s@IncWordCount(word) =>
      log.info(s"Got IncWordCount command, $self")
//      persist(s) { s =>
        val count = words.getOrElse(word, 0)
        words += (word -> (count + 1))
//      }
    case GetWordCount(word) => words.getOrElse(word, 0)
  }

//  override def receiveRecover = receiveCommand
}

object StatefulActor {

  sealed trait WordMessage {

    val word: String
  }

  case class IncWordCount(word: String) extends WordMessage

  case class GetWordCount(word: String) extends WordMessage

  val messageExtractor: ShardRegion.MessageExtractor = new HashCodeMessageExtractor(5) {
    override def entityId(message: Any) = {
      message match {
        case m: WordMessage => (Math.abs(m.word.hashCode) % 10).toString
        case m: Any => (Math.abs(m.hashCode) % 10).toString
      }
    }
  }

  val entityIdExtractor: ShardRegion.ExtractEntityId = {
    case m: WordMessage => ((Math.abs(m.word.hashCode) % 10).toString, m)
  }

  val shardIdExtractor: ShardRegion.ExtractShardId = {
    case m: WordMessage => (Math.abs(m.word.hashCode) % 3).toString
  }
}

