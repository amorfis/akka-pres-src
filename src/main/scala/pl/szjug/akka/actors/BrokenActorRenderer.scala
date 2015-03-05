package pl.szjug.akka.actors

import pl.szjug.fractals.{Job, JuliaRenderer}

import scala.util.Random

class BrokenActorRenderer extends ActorRenderer {

   override def receive = {
     case j: Job =>
       if (Random.nextInt(4) == 0) {
         throw new RuntimeException("Oops, actor failed")
       }
       val renderer = new JuliaRenderer(j, j.imgRegion)
       // Actor is blocked here
       val pixels = renderer.render()
       sender ! pixels
   }
 }

