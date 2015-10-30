package com.github.dmitraver.quikka

import akka.actor.Actor
import akka.io.IO

class Main extends Actor {
  import context.system

  IO(Quic) ! "Hello"

  override def receive: Receive = {
    case _ =>
  }
}
