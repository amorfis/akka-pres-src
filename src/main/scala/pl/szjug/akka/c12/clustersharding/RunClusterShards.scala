package pl.szjug.akka.c12.clustersharding

import akka.actor.{ActorIdentity, Identify, ActorPath, Actor, ActorSystem, Props}
import akka.cluster.sharding.{ClusterShardingSettings, ClusterSharding}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import StatefulActor.IncWordCount
import pl.szjug.util.NetworkUtil
import akka.pattern.ask
import scala.concurrent.duration._

import scala.util.Random

object RunClusterShards extends App with LazyLogging with NetworkUtil {

  val alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray

  def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
    import system.dispatcher
    implicit val timeout = Timeout(15.seconds)

    // Start the shared journal one one node (don't crash this SPOF)
    // This will not be needed with a distributed journal
    if (startStore)
      system.actorOf(Props[SharedLeveldbStore], "store")
    // register the shared journal
    val f = system.actorSelection(path) ? Identify(None)
    f.onSuccess {
      case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
      case _ =>
        system.log.error("Shared journal not started at {}", path)
        system.terminate()
    }
    f.onFailure {
      case _ =>
        system.log.error("Lookup of shared journal at {} timed out", path)
        system.terminate()
    }
  }

  def runActorSystem(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load("shard-cluster-application.conf"))

    val system = ActorSystem(s"ClusterSystem", config)

    startupSharedJournal(system, startStore = port == 2552, path =
      ActorPath.fromString("akka.tcp://ClusterSystem@127.0.0.1:2552/user/store"))

    system
  }

  val system1 = runActorSystem(2552)
  val system2 = runActorSystem(2553)
  val system3 = runActorSystem(2661)

  val settings = ClusterShardingSettings.create(system1)
  ClusterSharding(system1).start(
    typeName = "wordCounter",
    entityProps = Props(classOf[StatefulActor]),
    settings,
    messageExtractor = StatefulActor.messageExtractor)
  ClusterSharding(system2).start(
    typeName = "wordCounter",
    entityProps = Props(classOf[StatefulActor]),
    settings,
    messageExtractor = StatefulActor.messageExtractor)
  ClusterSharding(system3).start(
    typeName = "wordCounter",
    entityProps = Props(classOf[StatefulActor]),
    settings,
    messageExtractor = StatefulActor.messageExtractor)

  val shardedActor = ClusterSharding(system1).shardRegion("wordCounter")

  // Wait for cluster to be UP
  Thread.sleep(10000L)
  println("Sending many messages")

  for(i <- 1 to 100) {
    shardedActor ! IncWordCount(Random.nextString(32))
  }

}
