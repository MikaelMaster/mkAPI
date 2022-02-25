package com.mikael.mkAPI.kotlin.spigot.api.manager

import com.mikael.mkAPI.kotlin.spigot.api.plugin.MKPluginInstance
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

open class EventsManager : Listener {

    var isRegistered: Boolean = false

    var plugin: JavaPlugin = defaultPlugin()

    private fun defaultPlugin(): JavaPlugin {
        return JavaPlugin.getProvidingPlugin(javaClass)
    }

    open fun register(plugin: MKPluginInstance) {
        registerListener(plugin.plugin as JavaPlugin)
    }

    open fun registerListener(plugin: JavaPlugin) {
        unregisterListener()
        this.plugin = plugin
        this.isRegistered = true
        Bukkit.getPluginManager().registerEvents(this, plugin)
    }

    fun unregisterListener() {
        if (!isRegistered) return
        HandlerList.unregisterAll(this)
        this.isRegistered = false
    }

}