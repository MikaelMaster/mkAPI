package com.mikael.mkAPI.kotlin.spigot.api.manager

import com.mikael.mkAPI.kotlin.spigot.api.modules.BukkitTimeHandler
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

open class TimeManager : EventsManager, Runnable, BukkitTimeHandler {

    var taskDuration: Long = 20

    var taskStart: Long = 0

    var taskUsed: BukkitTask? = null

    fun existsTask(): Boolean {
        return taskUsed != null
    }

    fun stopTask() {
        if (existsTask()) {
            try {
                val id = taskUsed!!.taskId
                taskUsed!!.cancel()
                Bukkit.getScheduler().cancelTask(id)
                taskUsed = null
            } catch (ex: NullPointerException) {
                ex.printStackTrace()
            }
        }
    }

    override fun run() {}

    override val pluginConnected: Plugin
        get() = plugin

    val isRunning: Boolean
        get() = existsTask() && Bukkit.getScheduler().isCurrentlyRunning(taskUsed!!.taskId)

    constructor(ticks: Long) {
        taskDuration = ticks
    }

    constructor(seconds: Int = 1) {
        taskDuration = 20L * seconds
    }

    fun syncDelay(): BukkitTask? {
        taskUsed = newTask(taskDuration, false, false, this)
        taskStart = System.currentTimeMillis()
        return taskUsed
    }

    fun syncTimer(): BukkitTask? {
        taskUsed = newTask(taskDuration, true, false, this)
        taskStart = System.currentTimeMillis()
        return taskUsed
    }

    fun asyncTimer(): BukkitTask? {
        taskUsed = newTask(taskDuration, true, true, this)
        taskStart = System.currentTimeMillis()
        return taskUsed
    }

    fun asyncDelay(): BukkitTask? {
        taskUsed = newTask(taskDuration, false, true, this)
        taskStart = System.currentTimeMillis()
        return taskUsed
    }
}