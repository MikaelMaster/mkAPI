package com.mikael.mkAPI.kotlin.utils

import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import net.eduard.api.lib.manager.EventsManager
import org.bukkit.entity.Entity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent

val invunerableEntities = mutableSetOf<Entity>()

fun Entity.isInvunerable(invunerable: Boolean) {
    if (invunerable) {
        invunerableEntities.add(this)
    } else {
        invunerableEntities.remove(this)
    }
}

class InvunerableEntitySystem : EventsManager() {

    init {
        SpigotMainKt.syncTimer(20 * 3, 20 * 3) {
            invunerableEntities.removeIf { it.isDead }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(e: EntityDamageEvent) {
        if (!invunerableEntities.contains(e.entity)) return
        e.isCancelled = true
    }

}