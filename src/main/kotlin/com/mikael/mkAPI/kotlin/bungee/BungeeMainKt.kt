package com.mikael.mkAPI.kotlin.bungee

import com.mikael.mkAPI.java.APIJavaUtils
import com.mikael.mkAPI.java.bungee.BungeeMain
import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.api.redis.RedisAPI
import com.mikael.mkAPI.kotlin.api.redis.RedisConnectionData
import com.mikael.mkAPI.kotlin.bungee.listener.BungeeGeneralListener
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import com.mikael.mkAPI.kotlin.spigot.api.apimanager
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
import java.io.File
import kotlin.concurrent.thread

val apiBungeeMain = resolve<BungeeMain>()

object BungeeMainKt : IPluginInstance {

    lateinit var manager: APIManager
    lateinit var config: Config

    init {
        Hybrid.instance = BungeeServer
    }

    fun onEnable() {
        val start = System.currentTimeMillis()

        APIJavaUtils.fastLog("§eIniciando carregamento...")
        resolvePut(BungeeMain.instance)
        HybridTypes // Carregamento de types 1
        store<RedisConnectionData>()

        APIJavaUtils.fastLog("§eCarregando diretórios...")
        storage()
        config = Config(BungeeMain.instance, "config.yml")
        config.saveConfig()
        reloadConfig() // x1
        reloadConfig() // x2
        StorageAPI.updateReferences()

        APIJavaUtils.fastLog("§eCarregando extras...")
        reload()

        manager = resolvePut(APIManager())
        DBManager.setDebug(false)
        manager.sqlManager = SQLManager(config["Database", DBManager::class.java])
        if (manager.sqlManager.dbManager.isEnabled) {
            APIJavaUtils.fastLog("§eEstabelecendo conexão com o MySQL...")
            apimanager.dbManager.openConnection()
            if (!apimanager.sqlManager.hasConnection()) error("Cannot connect to database server")
            APIJavaUtils.fastLog("§aConexão estabelecida com o MySQL!")
        } else {
            APIJavaUtils.fastLog("§cO MySQL não está ativo na Config. Alguns plugins e sistemas MK podem não funcionar corretamente.")
        }

        RedisAPI.managerData = SpigotMainKt.config["Redis", RedisConnectionData::class.java]
        if (RedisAPI.managerData.isEnabled) {
            APIJavaUtils.fastLog("§eEstabelecendo conexão com servidor Redis...")
            RedisAPI.createClient(RedisAPI.managerData)
            RedisAPI.connectClient()
            if (!RedisAPI.isInitialized()) error("Cannot connect to Redis server")
            APIJavaUtils.fastLog("§aConexão estabelecida com o servidor Redis!")
        } else {
            APIJavaUtils.fastLog("§cO Redis não está ativo na Config. Alguns plugins e sistemas MK podem não funcionar corretamente.")
        }

        // BungeeAPI
        BungeeAPI.bungee.register(BungeeMain.instance)

        APIJavaUtils.fastLog("§eCarregando sistemas...")
        BungeeGeneralListener().register(apiBungeeMain)

        val endTime = System.currentTimeMillis() - start
        APIJavaUtils.fastLog("§aPlugin ativado com sucesso! (Tempo levado: §f${endTime}ms§a)")

        // MySQL queue update timer
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
        APIJavaUtils.fastLog("§eDescarregando sistemas...")
        apimanager.dbManager.closeConnection()
        RedisAPI.finishConnection()
        APIJavaUtils.fastLog("§cPlugin desativado!")
    }

    private fun storage() {
        StorageAPI.setDebug(false)
        StorageAPI.startGson()
    }

    private fun reloadConfig() {
        /*
        config.setHeader(
            "Durante a configuração deste arquivo, para colocar cores em textos você deve utilizar '§' e não '&'.",
            "Também vale lembrar que não será necessário colocar textos entre aspas."
        )
         */
        config.add(
            "Database",
            DBManager(),
            "Configurações do MySQL.",
            "A database informada abaixo será utilizada para todos os plugins MK."
        )
        config.add(
            "Redis",
            RedisConnectionData(),
            "Configurações do Redis.",
            "O servidor Redis informado abaixo será utilizado para todos os plugins MK."
        )
        config.saveConfig()
    }

    private fun reload() {
        Config.isDebug = false
        Copyable.setDebug(false)
    }

    override fun getPlugin(): Any {
        return this
    }

    override fun getSystemName(): String {
        return apiBungeeMain.description.name
    }

    override fun getPluginFolder(): File {
        return apiBungeeMain.file
    }

}