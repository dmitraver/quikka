package com.github.dmitraver.quikka

import akka.actor.{Actor, ActorLogging}

class QuicManager extends Actor with ActorLogging {

  override def receive: Receive = {
    case "Hello" => println("Hello")
  }
}
