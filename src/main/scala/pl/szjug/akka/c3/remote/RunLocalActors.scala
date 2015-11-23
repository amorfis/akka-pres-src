package pl.szjug.akka.c3.remote

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
import scala.util.{Failure, Success}

object RunLocalActors extends App with LazyLogging {

  val imageSize = Size2i(80, 40)
  val remoteHost = ConfigFactory.load("hosts.conf").getString("remote.host")
  implicit val timeout: Timeout = 3 seconds

  val config = ConfigFactory.load("remote-actor-refs.conf")
  val system = ActorSystem("actorSystem", config)

  val remoteRenderer = system.actorSelection(s"akka.tcp://remoteActorSystem@$remoteHost:2552/user/remoteRenderer")

  val future = remoteRenderer ? Identify(0)

  future.onComplete({
    case Success(ActorIdentity(_, Some(ref))) =>
      val master = system.actorOf(Props(classOf[SimpleActorMaster], imageSize, ref))
      master ! Job(imageSize, Region2i(imageSize), Palette)

    case Failure(e) => logger.error("Could not get remote actor ActorRef", e)
  })
}
