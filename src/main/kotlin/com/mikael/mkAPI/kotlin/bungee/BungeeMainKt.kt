package com.mikael.mkAPI.kotlin.bungee

import com.mikael.mkAPI.java.APIJavaUtils
import com.mikael.mkAPI.java.bungee.BungeeMain
import com.mikael.mkAPI.java.spigot.SpigotMain
import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.api.redis.RedisAPI
import com.mikael.mkAPI.kotlin.api.redis.RedisConnectionData
import com.mikael.mkAPI.kotlin.bungee.listener.BungeeGeneralListener
import com.mikael.mkAPI.kotlin.spigot.api.apimanager
import com.mikael.mkAPI.kotlin.spigot.api.plugin.MKPluginInstance
import net.eduard.api.lib.bungee.BungeeAPI
import net.eduard.api.lib.config.Config
import net.eduard.api.lib.database.DBManager
import net.eduard.api.lib.database.HybridTypes
import net.eduard.api.lib.database.SQLManager
import net.eduard.api.lib.hybrid.BungeeServer
import net.eduard.api.lib.hybrid.Hybrid
import net.eduard.api.lib.kotlin.register
import net.eduard.api.lib.kotlin.resolve
import net.eduard.api.lib.kotlin.resolvePut
import net.eduard.api.lib.kotlin.store
import net.eduard.api.lib.modules.Copyable
import net.eduard.api.lib.plugin.IPluginInstance
import net.eduard.api.lib.storage.StorageAPI
import net.md_5.bungee.api.ProxyServer
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

val apiBungeeMain = resolve<BungeeMain>()

object BungeeMainKt : MKPluginInstance {

    lateinit var manager: APIManager
    lateinit var config: Config

    init {
        Hybrid.instance = BungeeServer
    }

    fun onEnable() {
        val start = System.currentTimeMillis()

        APIJavaUtils.fastLog("§eStarting loading...")
        resolvePut(BungeeMain.instance)
        HybridTypes // Carregamento de types 1
        store<RedisConnectionData>()

        APIJavaUtils.fastLog("§eloading directories...")
        storage()
        config = Config(BungeeMain.instance, "config.yml")
        config.saveConfig()
        reloadConfig() // x1
        reloadConfig() // x2
        StorageAPI.updateReferences()

        APIJavaUtils.fastLog("§eLoading extras...")
        reload()

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
            ProxyServer.getInstance().scheduler.schedule(
                BungeeMain.instance, {
                    if (!RedisAPI.testPing()) {
                        try {
                            RedisAPI.connectClient(true)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            error("Cannot reconnect to Redis server")
                        }
                    }
                }, 1, 1, TimeUnit.SECONDS
            )
        } else {
            APIJavaUtils.fastLog("§cThe Redis is not active on the config file. Some plugins and MK systems may not work correctly.")
        }

        // BungeeAPI
        BungeeAPI.bungee.register(BungeeMain.instance)

        APIJavaUtils.fastLog("§eLoading systems...")
        BungeeGeneralListener().register(apiBungeeMain)

        val endTime = System.currentTimeMillis() - start
        APIJavaUtils.fastLog("§aPlugin loaded with success! (Time taken: §f${endTime}ms§a)")

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

    fun onDisable() {
        APIJavaUtils.fastLog("§eUnloading systems...")
        apimanager.dbManager.closeConnection()
        RedisAPI.finishConnection()
        APIJavaUtils.fastLog("§cPlugin unloaded!")
    }

    private fun storage() {
        StorageAPI.setDebug(false)
        StorageAPI.startGson()
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
        config.saveConfig()
    }

    private fun reload() {
        Config.isDebug = false
        Copyable.setDebug(false)
    }

    override val plugin: Any?
        get() = SpigotMain.getPlugin(SpigotMain::class.java)

    override val systemName: String?
        get() = SpigotMain.getPlugin(SpigotMain::class.java).name

    override val pluginFolder: File?
        get() = SpigotMain.getPlugin(SpigotMain::class.java).dataFolder
}