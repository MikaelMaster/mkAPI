package com.mikael.mkAPI.kotlin.spigot.listener

import com.mikael.mkAPI.java.spigot.SpigotMain
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class CustomCommandListener : EventsManager() {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun versionCommand(e: PlayerCommandPreprocessEvent) {
        if (!e.message.equals("/mkapi", true)) return
        val player = e.player
        val mkAPIPlugin = SpigotMain.getPlugin(SpigotMain::class.java)
        player.sendMessage("§a${mkAPIPlugin.name} §ev${mkAPIPlugin.description.version}} §f- §bdesenvolvido por Mikael.")
    }

}