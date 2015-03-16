package pl.szjug.akka.actors

import akka.testkit.TestActor.AutoPilot
import com.mkrcah.fractals._
import pl.szjug.akka.c4.manyactors.ManyActorsMaster
import pl.szjug.fractals.{Result, Job}
import scala.concurrent.duration._

import org.scalatest.{WordSpecLike, Matchers}

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit._

class ActorRendererTest extends TestKit(ActorSystem())
  with DefaultTimeout with ImplicitSender
  with WordSpecLike with Matchers {

  "ActorRenderer" should {
    "Respond with the same message it receives" in {
      // Given
      val actorRenderer = TestActorRef(Props[ActorRenderer])
      val size = Size2i(2, 2)

      // When
      actorRenderer ! Job(size, Region2i(size), HuePalette)

      // Then
      val expectedResult = Result(
        Seq(
          (Point2i(0, 0), ColorRGB(65280, 1536, 0)),
          (Point2i(1, 0), ColorRGB(65280, 33792, 0)),
          (Point2i(0, 1), ColorRGB(50688, 65280, 0)),
          (Point2i(1, 1), ColorRGB(65280, 50688, 0))))
      expectMsg(expectedResult)
    }
  }

  "ManyActorsMaster" should {
    "Send jobs to workers from cluster" in {
      val workersProbes = for(i <- 1 to 3) yield { TestProbe() }
      val workersRefs = workersProbes.map(_.ref)
      val imgSize = Size2i(10, 10)

      workersProbes.foreach(_.setAutoPilot(new AutoPilot {
        override def run(sender: ActorRef, msg: Any) = {
          testActor ! msg
          this
        }
      }))

      val master = TestActorRef(Props(new ManyActorsMaster(imgSize, workersRefs)))

      master ! Job(imgSize, Region2i(imgSize), HuePalette)

      for(i <- 1 to 3) {
        expectMsgPF(2 seconds) {
          case j: Job => true
        }
      }
    }
  }
}
