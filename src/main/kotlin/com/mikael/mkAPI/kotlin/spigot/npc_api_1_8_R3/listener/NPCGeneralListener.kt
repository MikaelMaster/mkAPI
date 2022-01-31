package com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3.listener

import com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3.PlayerNpcAPI
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent

class NPCGeneralListener : EventsManager() {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        PlayerNpcAPI.npcs.values.forEach { npc ->
            npc.showFor(player)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun noDamageNPC(e: EntityDamageByEntityEvent) {
        if (e.entity !is Player) return
        val player = e.entity as Player
        if (!PlayerNpcAPI.isNPC(player)) return
        e.isCancelled = true
    }

}