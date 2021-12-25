package com.mikael.mkAPI.kotlin

import net.eduard.api.lib.game.Particle
import net.eduard.api.lib.game.ParticleType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun <T : ItemStack> T.notBreakable(): T {
    val meta = itemMeta
    meta.spigot().isUnbreakable = true
    itemMeta = meta
    return this
}

fun Location.smokeDenyBuild(player: Player) {
    Particle(ParticleType.LARGE_SMOKE, 1).create(player, this.clone().add(0.5, 1.0, 0.5))
}

fun testLag(msg: String, thing: () -> Unit) {
    val start = System.currentTimeMillis()
    thing.invoke()
    val end = System.currentTimeMillis() - start
    Bukkit.getConsoleSender().sendMessage("§b[mkAPI] §6[LAG] §f${msg}: §c${end}ms")
}