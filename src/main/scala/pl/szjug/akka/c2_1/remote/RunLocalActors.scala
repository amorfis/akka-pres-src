package pl.szjug.akka.c2_1.remote

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.mkrcah.fractals._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import pl.szjug.akka.Constants._
import pl.szjug.akka.c2.simpleactor.SimpleActorMaster
import pl.szjug.fractals.Job
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object RunLocalActors extends App with LazyLogging {

  val imageSize = Size2i(80, 40)
  val remoteHost = ConfigFactory.load("remote-on-virtual.conf").getString("remote.netty.tcp.hostname")

  val config = ConfigFactory.load("remote-single-application-host.conf")
  val system = ActorSystem("remoteActorSystem", config)

  logger.info("Created local ActorSystem. Connecting to remote actors.")

  val remoteRenderer = system.actorSelection(s"akka.tcp://remoteActorSystem@$remoteHost:2552/user/remoteRenderer")

  implicit val timeout: Timeout = 3 seconds

  val f = remoteRenderer ? Identify(0)
  f.onSuccess({
    case ActorIdentity(_, Some(ref)) =>
      val master = system.actorOf(Props(classOf[SimpleActorMaster], imageSize, ref))
      master ! Job(imageSize, Region2i(imageSize), palette, quality)

    case a: Any => logger.error(s"Returned $a instead of ActorIdentity")
  })
  f.onFailure({
    case e: Exception => logger.error("Could not get remote actor ActorRef", e)
  })
}
