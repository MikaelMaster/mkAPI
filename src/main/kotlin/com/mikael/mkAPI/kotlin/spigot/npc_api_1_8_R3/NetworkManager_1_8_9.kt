package com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3

import net.minecraft.server.v1_8_R3.*

class NetworkManager_1_8_9(enumprotocoldirection: EnumProtocolDirection?) :
    NetworkManager(enumprotocoldirection) {

    /**
     * NÃ£o sei oque isso faz mais o Citizens deixa 'true'
     */
    override fun g(): Boolean {
        return true
    }
/*
    override fun a(i: Int) {

    }


    override fun c(): Boolean {
        return false
    }

    override fun l() {

    }

    override fun a() {

    }

    override fun a(
        packet: Packet<*>?,
        genericfuturelistener: GenericFutureListener<out Future<in Void>>?,
        vararg agenericfuturelistener: GenericFutureListener<out Future<in Void>>?
    ) {

    }

    override fun a(channelhandlercontext: ChannelHandlerContext?, packet: Packet<*>?) {
      }

    override fun a(secretkey: SecretKey?) {

    }

    override fun a(packetlistener: PacketListener?) {

    }

    override fun a(enumprotocol: EnumProtocol?) {

    }
    */
}