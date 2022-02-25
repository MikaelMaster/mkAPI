package com.mikael.mkAPI.kotlin.spigot.api.manager

import org.bukkit.entity.Player
import java.util.*

open class CooldownManager(var duration: Long = 20) {

    var msgOnCooldown: String = ""
    var msgCooldownOver: String = ""
    var msgCooldownStart: String = ""

    init {
        msgOnCooldown = "§cAguarde §e%times §cpara utilizar novamente."
    }

    fun noMessages() {
        msgOnCooldown = ""
        msgCooldownOver = ""
        msgCooldownStart = ""
    }

    val cooldowns = mutableMapOf<UUID, TimeManager>()

    fun cooldown(player: Player): Boolean {
        if (onCooldown(player)) {
            sendOnCooldown(player)
            return false
        }
        setOnCooldown(player)
        sendStartCooldown(player)
        return true
    }

    fun stopCooldown(player: Player) {
        val timeManager = cooldowns[player.uniqueId]
        timeManager?.stopTask()
        cooldowns.remove(player.uniqueId)
    }

    fun onCooldown(player: Player): Boolean {
        return getResult(player) > 0
    }

    inner class CooldownOverTask(val player: Player) : TimeManager(duration) {
        override fun run() {
            sendOverCooldown(player)
        }
        init {
            asyncDelay()
        }
    }

    fun setOnCooldown(player: Player): CooldownManager {
        if (onCooldown(player)) {
            stopCooldown(player)
        }
        cooldowns[player.uniqueId] = CooldownOverTask(player)
        return this
    }

    fun sendOverCooldown(player: Player) {
        if (msgCooldownOver.isNotEmpty())
            player.sendMessage(msgCooldownOver)
    }

    fun sendOnCooldown(player: Player) {
        if (msgOnCooldown.isNotEmpty()) {
            player.sendMessage(msgOnCooldown.replace("%time", "${getCooldown(player)}"))
        }
    }

    fun sendStartCooldown(player: Player) {
        if (msgCooldownStart.isNotEmpty()) {
            player.sendMessage(msgCooldownStart)
        }
    }

    fun getResult(player: Player): Long {
        if (cooldowns.containsKey(player.uniqueId)) {
            val now = System.currentTimeMillis()
            val timeManager = cooldowns[player.uniqueId]!!
            val before = timeManager.taskStart
            val cooldownDuration = timeManager.taskDuration * 50
            val endOfCooldown = before + cooldownDuration
            val durationLeft = endOfCooldown - now
            return if (durationLeft <= 0) {
                0
            } else durationLeft / 50
        }
        return 0
    }

    fun getCooldown(player: Player): Int {
        return (getResult(player) / 20).toInt() + 1
    }
}