package com.github.dmitraver.quikka

import akka.actor.{Props, Actor, ActorLogging}
import com.github.dmitraver.quikka.Quic.Bind

class QuicManager extends Actor with ActorLogging {

  override def receive: Receive = {
    case b: Bind => context.actorOf(Props(classOf[QuicListener], b))
  }
}
