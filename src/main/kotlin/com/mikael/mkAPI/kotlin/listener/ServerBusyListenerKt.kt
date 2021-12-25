package com.mikael.mkAPI.kotlin.listener

import com.mikael.mkAPI.java.Main
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class ServerBusyListenerKt : EventsManager() {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun disallowOnServerBusy(e: AsyncPlayerPreLoginEvent) {
        if (!Main.serverEnabled) {
            e.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                "§cO servidor ainda não está disponível. Tente novamente em alguns instantes!"
            )
        }
    }

}