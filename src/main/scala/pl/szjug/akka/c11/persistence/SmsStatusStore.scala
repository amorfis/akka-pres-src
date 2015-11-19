package pl.szjug.akka.c11.persistence

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import pl.szjug.akka.c11.persistence.SmsStatusStore.{SmsStatusUpdate, GetSmsStatusResponse, GetSmsStatus}

class SmsStatusStore extends PersistentActor with ActorLogging {



  override def persistenceId = "sms-status-store"

  var state = Map.empty[String, SmsStatus]

  override def receiveRecover = {
    case SmsStatusUpdate(id) => state = state + (id -> SmsStatusStore.getNextStatus(state.get(id)))
  }

  override def receiveCommand = {
    case GetSmsStatus(id) => sender ! GetSmsStatusResponse(id, state.get(id))
    case e@SmsStatusUpdate(id) =>
      persistAsync(e) { ssu =>
        state = state + (id -> SmsStatusStore.getNextStatus(state.get(id)))
      }
  }
}

object SmsStatusStore {

  case class SmsStatusUpdate(id: String)
  case class GetSmsStatus(id: String)
  case class GetSmsStatusResponse(id: String, status: Option[SmsStatus])

  val statusesSeq = Seq(Received, Processing, Processed, Sent, Delivered)

  def getNextStatus(prev: Option[SmsStatus]): SmsStatus = {
    prev.fold[SmsStatus]({
      Received
    })({ status: SmsStatus =>
      status match {
        case Delivered => Delivered
        case _ => statusesSeq(statusesSeq.indexOf(status) + 1)
      }
    })
  }
}

sealed trait SmsStatus
case object Received extends SmsStatus
case object Processing extends SmsStatus
case object Processed extends SmsStatus
case object Sent extends SmsStatus
case object Delivered extends SmsStatus