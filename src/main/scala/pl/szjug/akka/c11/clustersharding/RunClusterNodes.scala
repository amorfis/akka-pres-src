package pl.szjug.akka.c11.clustersharding

import akka.actor.{ActorSystem, Props}
import akka.contrib.pattern.ClusterSharding
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.c11.clustersharding.StatefulActor.IncWordCount
import pl.szjug.util.NetworkUtil

import scala.util.Random

object RunClusterNodes extends App with LazyLogging with NetworkUtil {

  val alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray

  def runActorSystem(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load("shard-cluster-application.conf"))

    ActorSystem("ClusterSystem", config)
  }

  val system1 = runActorSystem(2552)
  val system2 = runActorSystem(2553)
  val system3 = runActorSystem(2661)

  // Create sharded actor on system1
  ClusterSharding(system1).start("wordCounter", Some(Props(classOf[StatefulActor])), StatefulActor.idExtractor, StatefulActor.shardRegionResolver)
  ClusterSharding(system2).start("wordCounter", Some(Props(classOf[StatefulActor])), StatefulActor.idExtractor, StatefulActor.shardRegionResolver)
  ClusterSharding(system3).start("wordCounter", Some(Props(classOf[StatefulActor])), StatefulActor.idExtractor, StatefulActor.shardRegionResolver)

  val shardedActor = ClusterSharding(system1).shardRegion("wordCounter")

  // Wait for cluster to be UP
  Thread.sleep(10000L)
  println("Sending many messages")

  for(i <- 1 to 100) {
    shardedActor ! IncWordCount(Random.nextString(32))
  }

//  import scala.concurrent.duration._
//  implicit val timeout: Timeout = 3 seconds
//
//  val dupaF = shardedActor ? GetWordCount(UUID.randomUUID().toString, "dupa")
//  val kupaF = shardedActor ? GetWordCount(UUID.randomUUID().toString, "kupa")
//  val gF = shardedActor ? GetWordCount(UUID.randomUUID().toString, "gówniarz")
//
//  println("Asking!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//
//  dupaF.onSuccess {
//    case count => println(s"dupa count: $count")
//  }
//  kupaF.onSuccess { case count => println(s"kupa count: $count") }
//  gF.onSuccess { case count => println(s"gówniarz count: $count") }
}

