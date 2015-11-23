package pl.szjug.akka.c13.clustersharding

import scala.collection.immutable.IndexedSeq
import scala.concurrent.Future
import akka.actor.ActorRef
import akka.cluster.sharding.ShardCoordinator.ShardAllocationStrategy
import akka.cluster.sharding.ShardRegion.ShardId

class CustomAllocationStrategy extends ShardAllocationStrategy {

  override def allocateShard(
    requester: ActorRef,
    shardId: ShardId,
    currentShardAllocations: Map[ActorRef, IndexedSeq[ShardId]]): Future[ActorRef] = {

    val shardRegionActor = currentShardAllocations.keys.head

    Future.successful(shardRegionActor)
  }

  override def rebalance(
    currentShardAllocations: Map[ActorRef, IndexedSeq[ShardId]],
    rebalanceInProgress: Set[ShardId]): Future[Set[ShardId]] = {

    Future.successful(Set.empty)
  }
}
