package com.mikael.mkAPI.kotlin.spigot.api.modules

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

interface BukkitTimeHandler {

    val pluginConnected: Plugin?

    fun asyncTimer(startTicks: Long, ticks: Long, runnable: Runnable?): BukkitTask? {
        return newTimer(startTicks, ticks, true, runnable)
    }

    fun syncTimer(startTicks: Long, ticks: Long, runnable: Runnable?): BukkitTask? {
        return newTimer(startTicks, ticks, false, runnable)
    }

    fun syncDelay(ticks: Long, runnable: Runnable?): BukkitTask? {
        return newDelay(ticks, false, runnable)
    }

    fun asyncDelay(ticks: Long, runnable: Runnable?): BukkitTask? {
        return newDelay(ticks, true, runnable)
    }

    fun asyncTask(runnable: Runnable?): BukkitTask? {
        return newTask(true, runnable)
    }

    fun syncTask(runnable: Runnable?): BukkitTask? {
        return newTask(false, runnable)
    }

    fun newTask(time: Long, repeat: Boolean, async: Boolean, task: Runnable?): BukkitTask? {
        return if (repeat) {
            if (async) {
                scheduler.runTaskTimerAsynchronously(pluginConnected, task, time, time)
            } else scheduler.runTaskTimer(pluginConnected, task, time, time)
        } else {
            if (async) {
                scheduler.runTaskLaterAsynchronously(pluginConnected, task, time)
            } else {
                scheduler.runTaskLater(pluginConnected, task, time)
            }
        }
    }

    fun newTimer(startTicks: Long, ticks: Long, async: Boolean, runnable: Runnable?): BukkitTask? {
        return if (async) scheduler.runTaskTimerAsynchronously(
            pluginConnected,
            runnable,
            startTicks,
            ticks
        ) else scheduler.runTaskTimer(pluginConnected, runnable, startTicks, ticks)
    }

    fun newTask(async: Boolean, runnable: Runnable?): BukkitTask? {
        return if (async) {
            scheduler.runTaskAsynchronously(pluginConnected, runnable)
        } else {
            scheduler.runTask(pluginConnected, runnable)
        }
    }

    fun newDelay(ticks: Long, async: Boolean, runnable: Runnable?): BukkitTask? {
        return if (async) scheduler.runTaskLaterAsynchronously(
            pluginConnected,
            runnable,
            ticks
        ) else scheduler.runTaskLater(pluginConnected, runnable, ticks)
    }

    val scheduler: BukkitScheduler
        get() = Bukkit.getScheduler()

    fun cancelAllTasks() {
        scheduler.cancelTasks(pluginConnected)
    }
}