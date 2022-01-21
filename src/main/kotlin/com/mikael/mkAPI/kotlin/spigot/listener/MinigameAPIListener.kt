package com.mikael.mkAPI.kotlin.spigot.listener

import com.mikael.mkAPI.kotlin.api.minigameProfileSyncKey
import com.mikael.mkAPI.kotlin.api.startUserOrUpdate
import net.eduard.api.lib.hybrid.PlayerUser
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class MinigameAPIListener : EventsManager() {

    @EventHandler(priority = EventPriority.LOWEST)
    fun downloadMinigameProfile(e: AsyncPlayerPreLoginEvent) {
        synchronized(minigameProfileSyncKey) {
            PlayerUser(e.name).startUserOrUpdate()
        }
    }

}