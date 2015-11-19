package pl.szjug.akka.c11.persistence


import java.util.concurrent.{TimeoutException, Executors, TimeUnit}
import scala.concurrent.{Await, ExecutionContext}
import scala.util.Random
import akka.actor.{ActorSystemImpl, Props, ActorSystem}
import akka.persistence.journal.leveldb.{SharedLeveldbStore, SharedLeveldbJournal}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import akka.pattern.ask


object RunPersistence extends App with LazyLogging {

  val UpdatesCount = 10
  val PossibleIdsCount = 100

  implicit val timeout = new Timeout(1, TimeUnit.SECONDS)
  implicit val executionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(10))

  val system = ActorSystem("PersistentActorsSystem", ConfigFactory.load("persistence-application.conf"))

  val store = system.actorOf(Props[SharedLeveldbStore], "store")
  SharedLeveldbJournal.setStore(store, system)

  val persistentActor = system.actorOf(Props[SmsStatusStore])

//  Thread.sleep(500)
//  for(i <- 1 to UpdatesCount) {
//    val id = Random.nextInt(PossibleIdsCount).toString
//    persistentActor ! SmsStatusStore.SmsStatusUpdate(id)
//  }

  Thread.sleep(500)
  for(i <- 1 to 10) {
    val id = Random.nextInt(PossibleIdsCount).toString
    val statusAsk = persistentActor ? SmsStatusStore.GetSmsStatus(id)

    statusAsk.onSuccess({
      case SmsStatusStore.GetSmsStatusResponse(id, status) =>
        logger.info(s"$id status is $status")
      case _ => logger.info(s"strange message received")
    })
  }
}
