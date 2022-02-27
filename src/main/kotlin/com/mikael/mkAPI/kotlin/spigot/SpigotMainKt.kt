package com.mikael.mkAPI.kotlin.spigot

import com.mikael.mkAPI.java.APIJavaUtils
import com.mikael.mkAPI.java.spigot.SpigotMain
import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.api.redis.RedisAPI
import com.mikael.mkAPI.kotlin.api.redis.RedisConnectionData
import com.mikael.mkAPI.kotlin.bungee.BungeeMainKt
import com.mikael.mkAPI.kotlin.objects.SpigotServerData
import com.mikael.mkAPI.kotlin.spigot.api.apimanager
import com.mikael.mkAPI.kotlin.spigot.api.hasVaultEconomy
import com.mikael.mkAPI.kotlin.spigot.api.plugin.MKPluginInstance
import com.mikael.mkAPI.kotlin.spigot.task.AutoUpdateMenusTask
import com.mikael.mkAPI.kotlin.spigot.task.PlayerTargetAtPlayerTask
import com.mikael.mkAPI.kotlin.spigot.listener.VersionCommandListener
import com.mikael.mkAPI.kotlin.spigot.listener.ServerBusyListener
import com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3.listener.NPCGeneralListener
import com.mikael.mkAPI.kotlin.utils.InvunerableEntitySystem
import net.eduard.api.core.BukkitReplacers
import net.eduard.api.lib.abstraction.Hologram
import net.eduard.api.lib.bungee.BungeeAPI
import net.eduard.api.lib.config.Config
import net.eduard.api.lib.database.BukkitTypes
import net.eduard.api.lib.database.DBManager
import net.eduard.api.lib.database.HybridTypes
import net.eduard.api.lib.database.SQLManager
import net.eduard.api.lib.hybrid.BukkitServer
import net.eduard.api.lib.hybrid.Hybrid
import net.eduard.api.lib.kotlin.resolvePut
import net.eduard.api.lib.kotlin.store
import net.eduard.api.lib.manager.CommandManager
import net.eduard.api.lib.menu.Menu
import net.eduard.api.lib.modules.*
import net.eduard.api.lib.score.DisplayBoard
import net.eduard.api.lib.storage.StorageAPI
import net.eduard.api.lib.storage.storables.BukkitStorables
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import kotlin.concurrent.thread

object SpigotMainKt : MKPluginInstance, BukkitTimeHandler {

    lateinit var manager: APIManager
    lateinit var config: Config
    lateinit var messages: Config

    init {
        Hybrid.instance = BukkitServer
    }

    fun onEnable() {
        val start = System.currentTimeMillis()

        APIJavaUtils.fastLog("§eStarting loading...")
        HybridTypes // Carregamento de types 1
        BukkitTypes.register() // Carregamento de types 2
        store<RedisConnectionData>()

        APIJavaUtils.fastLog("§eLoading directories...")
        storage()
        config = Config(SpigotMain.getPlugin(SpigotMain::class.java), "config.yml")
        config.saveConfig()
        reloadConfig() // x1
        reloadConfig() // x2
        messages = Config(SpigotMain.getPlugin(SpigotMain::class.java), "messages.yml")
        messages.saveConfig()
        reloadMessages()// x1
        reloadMessages()// x2
        StorageAPI.updateReferences()

        APIJavaUtils.fastLog("§eLoading extras...")
        reload()
        APIJavaUtils.fastLog("§eStarting tasks...")
        tasks()

        APIJavaUtils.fastLog("§eLoading VaultAPI...")
        if (!hasVaultEconomy()) {
            APIJavaUtils.fastLog("§cCan't get 'Vault' plugin or can't get their economy. Some plugins and MK systems may not work correctly.")
        }
        VaultAPI.setupVault()

        // BukkitBungeeAPI
        BukkitBungeeAPI.register(SpigotMain.getPlugin(SpigotMain::class.java))
        BukkitBungeeAPI.requestCurrentServer()
        BungeeAPI.bukkit.register(SpigotMain.getPlugin(SpigotMain::class.java))

        manager = resolvePut(APIManager())
        DBManager.setDebug(false)
        manager.sqlManager = SQLManager(config["Database", DBManager::class.java])
        if (manager.sqlManager.dbManager.isEnabled) {
            APIJavaUtils.fastLog("§eConnecting to MySQL database...")
            apimanager.dbManager.openConnection()
            if (!apimanager.sqlManager.hasConnection()) error("Cannot connect to MySQL database")
            APIJavaUtils.fastLog("§aConnected to MySQL database!")
        } else {
            APIJavaUtils.fastLog("§cThe MySQL is not active on the config file. Some plugins and MK systems may not work correctly.")
        }

        RedisAPI.managerData = config["Redis", RedisConnectionData::class.java]
        if (RedisAPI.managerData.isEnabled) {
            APIJavaUtils.fastLog("§eConnecting to Redis server...")
            RedisAPI.createClient(RedisAPI.managerData)
            RedisAPI.connectClient()
            if (!RedisAPI.isInitialized()) error("Cannot connect to Redis server")
            APIJavaUtils.fastLog("§aConnected to Redis server!")
            object : BukkitRunnable() {
                override fun run() {
                    if (!RedisAPI.testPing()) {
                        try {
                            RedisAPI.connectClient(true)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            error("Cannot reconnect to Redis server")
                        }
                    }
                }
            }.runTaskTimer(SpigotMain.getPlugin(SpigotMain::class.java), 20, 20)
        } else {
            APIJavaUtils.fastLog("§cThe Redis is not active on the config file. Some plugins and MK systems may not work correctly.")
        }

        if (config.getBoolean("BungeeAPI.isEnable")) {
            APIJavaUtils.fastLog("§eCarregando BungeeAPI...")
            bungeeAPI()
        }

        APIJavaUtils.fastLog("§eLoading systems...")
        ServerBusyListener().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))
        VersionCommandListener().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))
        NPCGeneralListener().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))
        InvunerableEntitySystem().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))

        val endTime = System.currentTimeMillis() - start
        APIJavaUtils.fastLog("§aPlugin loaded with success! (Time taken: §f${endTime}ms§a)")

        syncDelay(20) {
            SpigotMain.serverEnabled = true
            APIJavaUtils.fastLog("§aThe server has been marked as available!")

            // MySQL queue updater timer
            if (apimanager.sqlManager.hasConnection()) {
                thread {
                    while (true) {
                        apimanager.sqlManager.runChanges()
                        Thread.sleep(1000)
                    }
                }
            }
        }
    }

    fun onDisable() {
        if (config.getBoolean("CustomKick.isEnabled")) {
            APIJavaUtils.fastLog("§eDisconnecting players...")
            for (playerLoop in Bukkit.getOnlinePlayers()) {
                playerLoop.kickPlayer(config.getString("CustomKick.customKickMessage"))
            }
        }
        APIJavaUtils.fastLog("§eUnloading systems...")
        BungeeAPI.controller.unregister()
        apimanager.dbManager.closeConnection()
        RedisAPI.finishConnection()
        APIJavaUtils.fastLog("§cPlugin unloaded!")
    }

    private fun bungeeAPI() {
        if (apimanager.sqlManager.hasConnection()) {
            if (config.getBoolean("BungeeAPI.useRedisCache")) {
                if (!RedisAPI.isInitialized()) error("Cannot get the Redis client for the BungeeAPI")
                // Continuação do código aqui
            }
            SpigotMain.mkBungeeAPIEnabled = true
            apimanager.sqlManager.createTable<SpigotServerData>()
            apimanager.sqlManager.createReferences<SpigotServerData>()
        } else {
            APIJavaUtils.fastLog("")
            APIJavaUtils.fastLog("§cCan't load the BungeeAPI. Turn on the MySQL in the config file of mkAPI and restart the server.")
            APIJavaUtils.fastLog("")
        }
    }

    private fun storage() {
        StorageAPI.setDebug(false)
        BukkitStorables.load()
        StorageAPI.startGson()
    }

    private fun reload() {
        Config.isDebug = false
        Menu.isDebug = false
        Hologram.debug = false
        CommandManager.debugEnabled = false
        Copyable.setDebug(false)
        BukkitBungeeAPI.setDebuging(false)
        DisplayBoard.colorFix = true
        DisplayBoard.nameLimit = 40
        DisplayBoard.prefixLimit = 16
        DisplayBoard.suffixLimit = 16
    }

    private fun tasks() {
        resetScoreboards()
        BukkitReplacers()
        if (config.getBoolean("MenuAPI.autoUpdateMenus")) {
            AutoUpdateMenusTask().asyncTimer()
        }
        PlayerTargetAtPlayerTask().asyncTimer()
    }

    private fun resetScoreboards() {
        for (teams in Mine.getMainScoreboard().teams) {
            teams.unregister()
        }
        for (objective in Mine.getMainScoreboard().objectives) {
            objective.unregister()
        }
        for (player in Mine.getPlayers()) {
            player.scoreboard = Mine.getMainScoreboard()
            player.maxHealth = 20.0
            player.health = 20.0
            player.isHealthScaled = false
        }
    }

    private fun reloadConfig() {
        config.add(
            "Database",
            DBManager(),
            "Config of MySQL database.",
            "All the plugins that use the mkAPI will use this MySQL database."
        )
        config.add(
            "Redis",
            RedisConnectionData(),
            "Config of Redis server.",
            "All the plugins that use the mkAPI will use this Redis server."
        )
        config.add(
            "MenuAPI.autoUpdateMenus",
            true,
            "Whether to update the open menus.",
        )
        config.add(
            "MenuAPI.autoUpdateTicks",
            60,
            "Time to refresh players open menus.",
            "Values less than 20 will cause server lag. 20 ticks = 1s."
        )
        config.add(
            "BungeeAPI.isEnabled", false, "Whether to activate the BungeeAPI."
        )
        config.add(
            "BungeeAPI.useRedisCache", false,
            "Whether to use Redis to improve the server performance.",
            "You CAN'T active this if you're not using a Redis servidor on mkAPI."
        )
        config.add(
            "BungeeAPI.currentServerName", "server",
            "The name of this spigot server before bungee.",
            "Put the server name as it is in the bungee config."
        )
        config.add(
            "BungeeAPI.currentServerMaxAmount",
            100,
            "Maximum number of players on this spigot server.",
        )
        config.add(
            "CustomKick.isEnabled",
            true,
            "Whether to kick players on server shutdown."
        )
        config.add(
            "CustomKick.customKickMessage",
            "§cRestarting, we'll back soon!",
            "Kick message sent to players on server shutdown."
        )
        config.saveConfig()
    }

    private fun reloadMessages() {
        messages.add("busy-server-msg", "§cThe server is busy. Try again in a few seconds.")
        messages.saveConfig()
    }

    override val plugin: Any?
        get() = SpigotMain.getPlugin(SpigotMain::class.java)

    override val systemName: String?
        get() = SpigotMain.getPlugin(SpigotMain::class.java).name

    override val pluginFolder: File?
        get() = SpigotMain.getPlugin(SpigotMain::class.java).dataFolder

    override fun getPluginConnected(): Plugin {
        return SpigotMain.getPlugin(SpigotMain::class.java)
    }
}