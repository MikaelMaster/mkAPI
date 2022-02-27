package com.mikael.mkAPI.kotlin.bungee.api.manager

import com.mikael.mkAPI.java.bungee.BungeeMain
import com.mikael.mkAPI.kotlin.bungee.api.toTextComponent
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*
import java.util.concurrent.TimeUnit

class BungeeCooldown(var duration: Long = 1000) {

    var msgCooldown: String = ""
    var msgOver: String = ""
    var msgStart: String = ""

    constructor(ticks: Int) : this(ticks * 50L)

    init {
        msgCooldown = "§cAguarde §e%times §cpara utilizar novamente."
    }

    fun noMessages() {
        msgCooldown = ""
        msgOver = ""
        msgStart = ""
    }

    val cooldowns = mutableMapOf<UUID, CooldownOverTask>()

    fun cooldown(player: ProxiedPlayer): Boolean {
        if (onCooldown(player)) {
            sendOnCooldown(player)
            return false
        }
        setOnCooldown(player)
        sendStartCooldown(player)
        return true
    }

    fun stopCooldown(player: ProxiedPlayer) {
        val timeManager = cooldowns[player.uniqueId]
        timeManager?.stop()
        cooldowns.remove(player.uniqueId)
    }

    fun onCooldown(player: ProxiedPlayer): Boolean {
        return getCooldownLeftMillis(player) > 0
    }

    inner class CooldownOverTask(val player: ProxiedPlayer) : Runnable {
        val startedAt = System.currentTimeMillis()
        val task = ProxyServer.getInstance().scheduler.schedule(
            BungeeMain.instance, this, duration,
            duration, TimeUnit.MILLISECONDS
        )

        fun stop() {
            task.cancel()
        }

        override fun run() {
            sendOverCooldown(player)
        }
    }

    fun setOnCooldown(player: ProxiedPlayer): BungeeCooldown {
        if (onCooldown(player)) {
            stopCooldown(player)
        }
        cooldowns[player.uniqueId] = CooldownOverTask(player)
        return this
    }

    fun sendOverCooldown(player: ProxiedPlayer) {
        if (msgOver.isNotEmpty())
            player.sendMessage(msgOver)
    }

    fun sendOnCooldown(player: ProxiedPlayer) {
        if (msgCooldown.isNotEmpty()) {
            player.sendMessage(msgCooldown.replace("%time", "${getCooldownLeftSeconds(player)}").toTextComponent())
        }
    }

    fun sendStartCooldown(player: ProxiedPlayer) {
        if (msgStart.isNotEmpty()) {
            player.sendMessage(msgStart.toTextComponent())
        }
    }

    fun getCooldownLeftMillis(player: ProxiedPlayer): Long {
        if (cooldowns.containsKey(player.uniqueId)) {
            val now = System.currentTimeMillis()
            val timeManager = cooldowns[player.uniqueId]!!
            val before = timeManager.startedAt
            val cooldownDuration = duration
            val endOfCooldown = before + cooldownDuration
            val durationLeft = endOfCooldown - now
            return if (durationLeft <= 0) {
                0
            } else durationLeft
        }
        return 0
    }

    fun getCooldownLeftSeconds(player: ProxiedPlayer): Int {
        return (getCooldownLeftMillis(player) / 1000).toInt() + 1
    }
}