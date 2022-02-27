package com.mikael.mkAPI.kotlin.bungee.listener

import com.mikael.mkAPI.kotlin.api.redis.RedisAPI
import com.mikael.mkAPI.kotlin.bungee.api.toTextComponent
import com.mikael.mkAPI.kotlin.bungee.apiBungeeMain
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeGeneralListener : Listener {

    @EventHandler
    fun versionCommand(e: ChatEvent) {
        if (!e.message.equals("/mkapiproxy", true)) return
        val player = e.sender as ProxiedPlayer
        player.sendMessage("§a${apiBungeeMain.description.name} §ev${apiBungeeMain.description.version} §f- §bdeveloped by Mikael.".toTextComponent())
        e.isCancelled = true
    }

    @EventHandler
    fun onPlayerJoin(e: PostLoginEvent) {
        if (!RedisAPI.isInitialized()) return
        RedisAPI.client!!.set("mkapi:bungee:players:${e.player.name.toLowerCase()}", "null")
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerDisconnectEvent) {
        if (!RedisAPI.isInitialized()) return
        RedisAPI.client!!.del("mkapi:bungee:players:${e.player.name.toLowerCase()}")
    }

    @EventHandler
    fun onServerChange(e: ServerConnectedEvent) {
        if (!RedisAPI.isInitialized()) return
        RedisAPI.client!!.set("mkapi:bungee:players:${e.player.name.toLowerCase()}", e.server.info.name)
    }

}