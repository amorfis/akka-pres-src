package pl.szjug.akka.c13.clustersharding

import akka.actor.{PoisonPill, ActorSystem, Props}
import akka.cluster.sharding.ShardRegion.HashCodeMessageExtractor
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants.{ImageSize, Palette}
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.akka.c12.clustersharding.ClusterShardingActorsMaster
import pl.szjug.fractals.{Job, JobToDivide}


object RunClusterShardsWithCustomStrategy extends App with LazyLogging {

  val ShardingTypeName = "rendering"

  def runActorSystem(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load("shard-cluster-application.conf"))

    ActorSystem(s"ClusterSystem", config)
  }

  val system1 = runActorSystem(2552)
  val system2 = runActorSystem(2553)
  val system3 = runActorSystem(2661)

  // Start the shared journal one one node (don't crash this SPOF)
  // This will not be needed with a distributed journal
  val storeRef = system1.actorOf(Props[SharedLeveldbStore], "store")
  SharedLeveldbJournal.setStore(storeRef, system1)
  SharedLeveldbJournal.setStore(storeRef, system2)
  SharedLeveldbJournal.setStore(storeRef, system3)

  val messageExtractor = new HashCodeMessageExtractor(100) {
    override def entityId(message: Any) = {
      message match {
        case Job(_, region, _) =>
          val entityId = (region.tl.x + region.tl.y).toString
          entityId
      }
    }
  }

  private val customAllocationStrategy = new CustomAllocationStrategy()

  ClusterSharding(system1).start(
    typeName = ShardingTypeName,
    entityProps = Props(classOf[ActorRenderer]),
    settings = ClusterShardingSettings.create(system1),
    messageExtractor = messageExtractor,
    allocationStrategy = customAllocationStrategy,
    handOffStopMessage = PoisonPill)
  ClusterSharding(system2).start(
    typeName = ShardingTypeName,
    entityProps = Props(classOf[ActorRenderer]),
    settings = ClusterShardingSettings.create(system2),
    messageExtractor = messageExtractor,
    allocationStrategy = customAllocationStrategy,
    handOffStopMessage = PoisonPill)
  ClusterSharding(system3).start(
    typeName = ShardingTypeName,
    entityProps = Props(classOf[ActorRenderer]),
    settings = ClusterShardingSettings.create(system3),
    messageExtractor = messageExtractor,
    allocationStrategy = customAllocationStrategy,
    handOffStopMessage = PoisonPill)

  // Wait for cluster to be UP
  Thread.sleep(15000L)
  println("Sending messages")

  val master = system1.actorOf(Props[ClusterShardingActorsMaster])

  master ! JobToDivide(ImageSize, 100, 20, Palette)
}
