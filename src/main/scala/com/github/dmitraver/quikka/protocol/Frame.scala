package com.github.dmitraver.quikka.protocol

import akka.util.ByteString

/**
 * Base trait for all supported QUIC frames.
 */
sealed trait Frame

/**
 * Frame that is used for implicit stream creation and sending data on it.
 * @param streamId unsigned id unique to this stream.
 * @param fin if true indicates that the sender is done sending data on this stream and wishes to "half-close" it.
 * @param offset unsigned number specifying the byte offset in the stream for this block of data.
 * @param data binary data that is supposed to be sent.
 */
case class StreamFrame(streamId: Int, fin: Boolean, offset: Long, data: ByteString) extends Frame

/**
 * Frame that is used to inform the peer that it shouldn't continue to wait for packets with packet numbers lower than a specified value.
 * @param leastUnackedDelta packet number delta that is used to determine least unacked packet by subtracting it from the headers packet number.
 */
case class StopWaitingFrame(leastUnackedDelta: Long) extends Frame

/**
 * Frame that is used to inform the peer of an increase in an endpoints flow control receive window.
 * @param streamId id of the stream whose flow control window is being updated or 0 to specify the connection level flow control window.
 * @param byteOffset absolute byte offset of data which can be sent on the given stream. In case of connection level flow control, the
 *                   cumulative number of bytes which can be sent on all currently open streams.
 */
case class WindowUpdateFrame(streamId: Int, byteOffset: Long) extends Frame

/**
 * Frame that is used to indicate to the remote endpoint that this endpoint is ready to send data (and has data to send) but is currently
 * flow control blocked.
 * @param streamId id of the stream whose flow control is blocked or 0 in case of connection level flow control.
 */
case class BlockedFrame(streamId: Int) extends Frame

/**
 * Frame that is used to pad a packet with 0x00 bytes. When this frame is encountered the rest of the packet is expected to be padding bytes.
 */
case class PaddingFrame() extends Frame

/**
 * Frame that is used to allow abnormal termination of the stream. When sent by the creator of the stream indicates the creator wishes
 * to cancel the stream. When sent by the receiver of a stream indicates an error or that the receiver did not want to accept the stream,
 * so the stream should be closed.
 * @param streamId id of the stream being terminated.
 * @param byteOffset absolute byte offset of the end of data for this stream.
 * @param errorCode code that indicates why the stream is closed.
 */
case class RSTStreamFrame(streamId: Int, byteOffset: Long, errorCode: Int) extends Frame

/**
 * Frame that is used by an endpoint to verify that a peer is still alive.
 */
case class PingFrame() extends Frame

/**
 * Frame that is used to notify that the connection is being closed.
 * @param errorCode code that indicates the reason for closing this connection.
 * @param reason optional human-readable explanation why the connection was closed.
 */
case class ConnectionCloseFrame(errorCode: Int, reason: Option[String]) extends Frame

/**
 * Frame that is used to notify that the connection should stop being used and will likely be aborted in future.
 * @param errorCode code that indicates the reason for closing this connection.
 * @param lastGoodStreamId last stream id which was accepted by the sender of this frame or 0 if no streams were replied to.
 * @param reason optional human-readable explanation why the connection was closed.
 */
case class GoAwayFrame(errorCode: Int, lastGoodStreamId: Int, reason: Option[String]) extends Frame
