package com.mikael.mkAPI.kotlin.spigot.listener

import com.mikael.mkAPI.java.spigot.SpigotMain
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class ServerBusyListener : EventsManager() {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun disallowOnServerBusy(e: AsyncPlayerPreLoginEvent) {
        if (!SpigotMain.serverEnabled) {
            e.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                SpigotMainKt.messages.getString("busy-server-msg")
            )
        }
    }

}