package com.github.dmitraver.quikka

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.io.{IO, Udp}

/**
 * Actor that listens for the incoming QUIC connections.
 */
class QuicListener(bindCommand: Quic.Bind) extends Actor with ActorLogging {
  import context.system

  val handler = bindCommand.handler

  context.watch(handler)

  IO(Udp) ! Udp.Bind(self, bindCommand.localAddress)

  override def receive: Receive = {

    case Udp.Bound(localAddress) =>
      handler ! Quic.Bound(localAddress)
      context become ready(sender())
    case Udp.CommandFailed(cmd) => handler ! Quic.CommandFailed(bindCommand)
  }

  def ready(sender: ActorRef): Actor.Receive = {
    case Quic.Unbind => sender ! Udp.Unbind
    case Udp.Unbound => context stop self
    case Udp.Received(data, remoteAddress) => // process QUIC packets here

  }
}
