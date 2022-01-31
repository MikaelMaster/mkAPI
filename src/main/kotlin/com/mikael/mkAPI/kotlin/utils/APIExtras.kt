package com.mikael.mkAPI.kotlin.utils

import net.eduard.api.lib.game.Particle
import net.eduard.api.lib.game.ParticleType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
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

fun Player.soundNo(volume: Float = 2f, speed: Float = 1f) {
    this.playSound(this.location, Sound.VILLAGER_NO, volume, speed)
}

fun Player.soundYes(volume: Float = 2f, speed: Float = 1f) {
    this.playSound(this.location, Sound.VILLAGER_YES, volume, speed)
}

fun Player.soundClick(volume: Float = 2f, speed: Float = 1f) {
    this.playSound(this.location, Sound.CLICK, volume, speed)
}

fun Player.soundPickup(volume: Float = 2f, speed: Float = 1f) {
    this.playSound(this.location, Sound.ITEM_PICKUP, volume, speed)
}

fun Player.soundPling(volume: Float = 2f, speed: Float = 1f) {
    this.playSound(this.location, Sound.NOTE_PLING, volume, speed)
}

fun Player.notify(volume: Float = 2f, speed: Float = 1f) {
    this.playSound(this.location, Sound.ORB_PICKUP, volume, speed)
}

fun testLag(msg: String, thing: () -> Unit) {
    val start = System.currentTimeMillis()
    thing.invoke()
    val end = System.currentTimeMillis() - start
    Bukkit.getConsoleSender().sendMessage("§b[mkAPI] §6[LAG] §f${msg}: §c${end}ms")
}