package com.github.dmitraver.quikka

import akka.actor._
import akka.io.IO

/**
 * Google QUIC protocol extension for Akka's IO layer.
 */
object Quic extends ExtensionId[QuicExt] with ExtensionIdProvider {

  override def createExtension(system: ExtendedActorSystem): QuicExt = new QuicExt(system)

  override def lookup(): ExtensionId[_ <: Extension] = Quic
}

class QuicExt(system: ExtendedActorSystem) extends IO.Extension {

  override def manager: ActorRef = {
    system.actorOf(
      props = Props(classOf[QuicManager]),
      name = "IO-QUIC"
    )
  }
}
