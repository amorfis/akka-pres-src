package pl.szjug.akka.actors

import akka.testkit.TestActor.AutoPilot
import com.mkrcah.fractals._
import pl.szjug.akka.c4.manyactors.ManyActorsMaster
import pl.szjug.fractals.{Result, Job}

import org.scalatest.{WordSpecLike, Matchers}

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.testkit._

class ActorRendererTest extends TestKit(ActorSystem())
  with DefaultTimeout
  with ImplicitSender
  with WordSpecLike
  with Matchers {

  "ActorRenderer" should {
    "Respond with the same message it receives" in {
      // Given
      val actorRenderer = TestActorRef(Props[ActorRenderer])
      println(actorRenderer.underlyingActor.getClass)

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
    "Send jobs to workers" in {
      val workersProbes = for(i <- 1 to 3) yield { TestProbe() }
      val workersRefs = workersProbes.map(_.ref)
      val imgSize = Size2i(10, 10)

      val master = TestActorRef(Props(new ManyActorsMaster(imgSize, workersRefs)))

      master ! Job(imgSize, Region2i(imgSize), HuePalette)

      workersProbes.foreach(_.expectMsgClass(classOf[Job]))
    }
  }

  "ManyActorsMaster" should {
    "Send jobs to workers and jobs regions should sum up to whole image" in {
      val workersProbes = for (i <- 1 to 3) yield {
        TestProbe()
      }
      val workersRefs = workersProbes.map(_.ref)
      val imgSize = Size2i(10, 10)

      var regions = Set[Region2i]()

      workersProbes.foreach(_.setAutoPilot(new AutoPilot {
        override def run(sender: ActorRef, msg: Any) = {
          regions += msg.asInstanceOf[Job].imgRegion
          this
        }
      }))

      val master = TestActorRef(Props(new ManyActorsMaster(imgSize, workersRefs)))

      master ! Job(imgSize, Region2i(imgSize), HuePalette)

      workersProbes.foreach(_.expectMsgClass(classOf[Job]))

      val minX = regions.map(_.tl.x).min
      val maxX = regions.map(_.br.x).max
      val minY = regions.map(_.tl.y).min
      val maxY = regions.map(_.br.y).min

      assert(minX == 0)
      assert(maxX == imgSize.width - 1)
      assert(minY == 0)
      assert(maxY == imgSize.height - 1)
    }
  }
}
