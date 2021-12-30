package com.mikael.mkAPI.kotlin

import com.mikael.mkAPI.java.Main
import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.api.apimanager
import com.mikael.mkAPI.kotlin.command.VersionCommand
import com.mikael.mkAPI.kotlin.listener.ServerBusyListenerKt
import net.eduard.api.lib.config.Config
import net.eduard.api.lib.database.DBManager
import net.eduard.api.lib.database.SQLManager
import net.eduard.api.lib.kotlin.resolvePut
import net.eduard.api.lib.modules.BukkitTimeHandler
import net.eduard.api.lib.plugin.IPlugin
import net.eduard.api.lib.storage.StorageAPI
import org.bukkit.plugin.Plugin
import java.io.File

object MainKt : IPlugin, BukkitTimeHandler {

    lateinit var manager: APIManager
    lateinit var config: Config

    fun onEnable() {
        val start = System.currentTimeMillis()

        manager = resolvePut(APIManager())
        Main.log("§eCarregando diretórios...")
        config = Config(Main.getPlugin(Main::class.java), "config.yml")
        config.saveConfig()
        reloadConfig()

        manager.sqlManager = SQLManager(config["database", DBManager::class.java])
        if (manager.sqlManager.dbManager.isEnabled) {
            Main.log("§eEstabelecendo conexão com o MySQL...")
            apimanager.dbManager.openConnection()
            if (!apimanager.sqlManager.hasConnection()) error("Cannot connect to database")
            Main.log("§aConexão estabelecida com o MySQL!")
        }

        Main.log("§eCarregando sistemas...")
        ServerBusyListenerKt().registerListener(Main.getPlugin(Main::class.java))
        VersionCommand().registerListener(Main.getPlugin(Main::class.java))

        val endTime = System.currentTimeMillis() - start
        Main.log("§aPlugin ativado com sucesso! (Tempo levado: §f$endTime§fms§a)")

        syncDelay(20L) {
            Main.serverEnabled = true
            Main.log("§aO servidor foi marcado como disponível!")
            asyncTimer(20L, 20L) {
                apimanager.sqlManager.runUpdatesQueue()
                apimanager.sqlManager.runDeletesQueue()
            }
        }
    }

    fun onDisable() {
        Main.log("§eDescarregando sistemas...")
        Main.log("§cPlugin desativado!")
    }

    private fun reloadConfig() {
        config.add(
            "database",
            DBManager(),
            "Configurações do MySQL.",
            "A database informada abaixo será utilizada para todos os plugins MK."
        )
        config.add(
            "database-update-limit",
            50,
            "Limite de atualizações por segundo no MySQL.",
            "Valores menores que 30 e maiores que 100 podem afetar o funcionamento dos sistemas."
        )
        config.saveConfig()
        StorageAPI.updateReferences()
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