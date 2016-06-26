package com.github.dmitraver.quikka

import java.net.InetSocketAddress

import akka.actor._
import akka.io.IO
import akka.util.ByteString

/**
 * Google QUIC protocol extension for Akka's IO layer.
 */
object Quic extends ExtensionId[QuicExt] with ExtensionIdProvider {

  override def createExtension(system: ExtendedActorSystem): QuicExt = new QuicExt(system)

  override def lookup(): ExtensionId[_ <: Extension] = Quic

  /**
   * Base trait for all QUIC related messages.
   */
  sealed trait Message

  /**
   * Base trait for all outbound messages that are sent by the clients to QUIC extension.
   */
  trait Command extends Message

  /**
   * Base trait for all inbound messages that are sent by the QUIC extension API to the clients.
   */
  trait Event extends Message

  /**
   * Sender actor will reply with this message whenever the command cannot be completed.
   * @param command original command that failed to be completed.
   */
  case class CommandFailed(command: Command) extends Event

  /**
   * The Bind message is send to the QUIC manager actor, which is obtained via
   * [[Quic#manager]] in order to bind to a listening socket. The manager
   * replies either with a [[CommandFailed]] or the actor handling the listen
   * socket replies with a [[Bound]] message. If the local port is set to 0 in
   * the Bind message, then the [[Bound]] message should be inspected to find
   * the actual port which was bound to.
   * @param handler actor which will receive all incoming connection requests
   *                in the form of [[Connected]] messages.
   * @param localAddress socket address to bind to; use port zero for
   *                automatic assignment (i.e. an ephemeral port, see [[Bound]])
   */
  final case class Bind(handler: ActorRef, localAddress: InetSocketAddress) extends Command

  /**
   * The sender of a [[Bind]] command will reply with this command in case the binding was successful.
   * @param localAddress socket address the listener actor is bound to. If the address provided in [[Bind]] command was
   *                     0 then it represents the port that was automatically assigned.
   */
  final case class Bound(localAddress: InetSocketAddress) extends Event

  /**
   * In order to close down a listening socket, send this message to that socket’s
   * actor (that is the actor which previously had sent the [[Bound]] message). The
   * listener socket actor will reply with a [[Unbound]] message.
   */
  case object Unbind extends Command

  /**
   * The sender of an `Unbind` command will receive confirmation through this
   * message once the listening socket has been closed.
   */
  case object Unbound extends Event

  /**
   * This message must be sent to a QUIC connection actor after receiving the
   * [[Connected]] message. The connection will not read any data from the
   * socket until this message is received, because this message defines the
   * actor which will receive all inbound data.
   *
   * @param handler The actor which will receive all incoming data and which
   *                will be informed when the connection is closed.
   **/
  final case class Register(handler: ActorRef) extends Command

  /**
   * The Connect message is sent to the QUIC manager actor, which is obtained via
   * [[QuicExt#manager]]. Either the manager replies with a [[CommandFailed]]
   * or the actor handling the new connection replies with a [[Connected]]
   * message.
   *
   * @param remoteAddress is the address to connect to
   */
  final case class Connect(remoteAddress: InetSocketAddress) extends Command

  /**
   * The connection actor sends this message either to the sender of a [[Connect]]
   * command (for outbound) or to the handler for incoming connections designated
   * in the [[Bind]] message. The connection is characterized by the `remoteAddress`
   * and `localAddress` QUIC endpoints.
   */
  final case class Connected(remoteAddress: InetSocketAddress, localAddress: InetSocketAddress) extends Event

  /**
   * Whenever data are read from a socket they will be transferred within this
   * class to the handler actor which was designated in the [[Register]] message.
   */
  final case class Received(data: ByteString) extends Event

  /**
   * Writes data to the QUIC connection.
   */
  final case class Write(data: ByteString) extends Command
}

class QuicExt(system: ExtendedActorSystem) extends IO.Extension {

  override def manager: ActorRef = {
    system.actorOf(
      props = Props(classOf[QuicManager]),
      name = "IO-QUIC"
    )
  }
}
