package pl.szjug.akka.c12.clustersharding


import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.ShardRegion.HashCodeMessageExtractor
import akka.cluster.sharding.{ShardRegion, ClusterShardingSettings, ClusterSharding}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants.{palette, imageSize}
import pl.szjug.akka.actors.ActorRenderer
import pl.szjug.fractals.{Job, JobToDivide}


object RunClusterShards extends App with LazyLogging {

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

  val messageExtractor: ShardRegion.MessageExtractor = new HashCodeMessageExtractor(10) {
    override def entityId(message: Any) = {
      message match {
        case Job(_, region, _) => (region.tl.x + region.tl.y).toString
      }
    }
  }

  ClusterSharding(system1).start(
    typeName = "rendering",
    entityProps = Props(classOf[ActorRenderer]),
    settings = ClusterShardingSettings.create(system1),
    messageExtractor = messageExtractor)
  ClusterSharding(system2).start(
    typeName = "rendering",
    entityProps = Props(classOf[ActorRenderer]),
    settings = ClusterShardingSettings.create(system2),
    messageExtractor = messageExtractor)
  ClusterSharding(system3).start(
    typeName = "rendering",
    entityProps = Props(classOf[ActorRenderer]),
    settings = ClusterShardingSettings.create(system3),
    messageExtractor = messageExtractor)

  val shardedActor = ClusterSharding(system1).shardRegion("rendering")

  // Wait for cluster to be UP
  Thread.sleep(10000L)
  println("Sending messages")

  val master = system1.actorOf(Props[ClusterShardingActorsMaster])

  master ! JobToDivide(imageSize, 100, 200, palette)
}
