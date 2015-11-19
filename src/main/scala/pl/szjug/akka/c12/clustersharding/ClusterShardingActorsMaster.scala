package pl.szjug.akka.c12.clustersharding

import akka.cluster.sharding.ClusterSharding
import pl.szjug.akka.actors.{JobHandling, PaintingResultsActor}
import pl.szjug.akka.Constants._

class ClusterShardingActorsMaster extends PaintingResultsActor with JobHandling {

  val Rows = 15
  val Columns = 40

  val sharded = ClusterSharding(context.system).shardRegion(RunClusterShards.ShardingTypeName)

  val handleJobs = handleJob(Seq(sharded))

  override val receive: Receive = handleJobs orElse paintResultPixels(imageSize)
}
