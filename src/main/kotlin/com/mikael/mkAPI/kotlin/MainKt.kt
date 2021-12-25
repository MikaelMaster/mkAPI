package com.mikael.mkAPI.kotlin

import com.mikael.mkAPI.java.Main
import com.mikael.mkAPI.kotlin.listener.ServerBusyListenerKt
import net.eduard.api.lib.modules.BukkitTimeHandler
import net.eduard.api.lib.plugin.IPlugin
import org.bukkit.plugin.Plugin
import java.io.File

object MainKt : IPlugin, BukkitTimeHandler {

    fun onEnable() {
        val start = System.currentTimeMillis()
        Main.log("§eCarregando sistemas...")
        ServerBusyListenerKt().registerListener(Main.getPlugin(Main::class.java))
        val endTime = System.currentTimeMillis() - start
        Main.log("§aPlugin ativado com sucesso! (Tempo levado: §f$endTime§fms§a)")

        syncDelay(20L) {
            Main.serverEnabled = true
            Main.log("§aO servidor foi marcado como disponível!")
        }
    }

    fun onDisable() {
        Main.log("§eDescarregando sistemas...")
        Main.log("§cPlugin desativado!")
    }

    override fun getPlugin(): Any {
        return Main.getPlugin(Main::class.java)
    }

    override fun getSystemName(): String {
        return Main.getPlugin(Main::class.java).name
    }

    override fun getPluginFolder(): File {
        return Main.getPlugin(Main::class.java).dataFolder
    }

    override fun getPluginConnected(): Plugin {
        return Main.getPlugin(Main::class.java)
    }
}