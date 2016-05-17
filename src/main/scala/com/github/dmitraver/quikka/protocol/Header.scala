package com.github.dmitraver.quikka.protocol

object Header {

  object PublicFlags {
    /**
     * Client: setting it indicates that the header contains a QUIC version. Must be set by a client in all packets
     * until confirmation from the server arrives agreeing to the proposed version is received by the client.
     *
     * Server: setting it indicates that the packet is Version Negotiation packet. Server indicates agreement on a
     * version by sending packets without setting this bit.
     */
    val PUBLIC_FLAG_VERSION = 0x01

    /**
     * Set to indicate that packet is a Public Reset packet.
     */
    val PUBLIC_FLAG_RESET = 0x02

    /**
     * Indicates the presence of a 32 byte diversification nonce in the header.
     */
    val NONCE_PRESENCE = 0x04

    /**
     * Indicates that full 8 byte Connection ID is present in the packet.
     */
    val CONNECTION_ID_FULL_SIZE = 0x08

    val PACKET_NUMBER_SIZE_6_BYTES = 0x30
    val PACKET_NUMBER_SIZE_4_BYTES = 0x20
    val PACKET_NUMBER_SIZE_2_BYTES = 0x10
    val PACKET_NUMBER_SIZE_1_BYTE = 0x00
  }

  object PrivateFlags {

    /**
     * For data packets, signifies that this packet contains the 1 bit of entropy.
     */
    val FLAG_ENTROPY = 0x01
  }

}
