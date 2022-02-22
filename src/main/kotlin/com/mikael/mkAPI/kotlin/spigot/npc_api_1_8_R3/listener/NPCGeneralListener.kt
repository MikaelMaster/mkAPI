package com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3.listener

import com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3.PlayerNPC
import com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3.PlayerNpcAPI
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent

class NPCGeneralListener : EventsManager() {

    private fun PlayerNPC.needShowNPC(player: Player, before: Location, after: Location) {
        if (this.getPlayer().world.name != player.world.name) return
        val oldDisctance = before.distance(this.getPlayer().location)
        val newDistance = after.distance(this.getPlayer().location)
        // Fiz uma modificação pra ficar entre 43 e 45. Antes era entre 44 e 45.
        if (newDistance < oldDisctance && newDistance > 43 && newDistance < 45) {
            this.showFor(player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        PlayerNpcAPI.npcs.values.forEach { npc ->
            npc.showFor(player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        val player = e.player
        PlayerNpcAPI.npcs.values.forEach { npc ->
            if (npc.getPlayer().world.name == player.world.name) {
                npc.showFor(player)
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        if (e.from.equals(e.to)) return
        PlayerNpcAPI.npcs.values.forEach { npc ->
            npc.needShowNPC(player, e.from, e.to)
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