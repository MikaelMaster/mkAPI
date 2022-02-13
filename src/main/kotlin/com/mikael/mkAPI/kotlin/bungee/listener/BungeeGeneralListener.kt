package com.mikael.mkAPI.kotlin.bungee.listener

import com.mikael.mkAPI.kotlin.bungee.api.toTextComponent
import com.mikael.mkAPI.kotlin.bungee.apiBungeeMain
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class BungeeGeneralListener : Listener {

    @EventHandler
    fun versionCommand(e: ChatEvent) {
        if (!e.message.equals("/mkapiproxy", true)) return
        val player = e.sender as ProxiedPlayer
        player.sendMessage("§a${apiBungeeMain.description.name} §ev${apiBungeeMain.description.version} §f- §bdesenvolvido por Mikael.".toTextComponent())
        e.isCancelled = true
    }

}