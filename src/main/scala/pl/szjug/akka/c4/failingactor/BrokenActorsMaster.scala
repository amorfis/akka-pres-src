package pl.szjug.akka.c4.failingactor

import com.mkrcah.fractals.Size2i
import pl.szjug.akka.actors.BrokenActorRenderer
import pl.szjug.akka.c3.manyactors.ManyActorsMaster

class BrokenActorsMaster(imgSize: Size2i) extends ManyActorsMaster(imgSize, classOf[BrokenActorRenderer])
