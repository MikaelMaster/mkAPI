package com.mikael.mkAPI.kotlin.bungee

import com.mikael.mkAPI.java.APIJavaUtils
import com.mikael.mkAPI.java.bungee.BungeeMain
import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import com.mikael.mkAPI.kotlin.spigot.api.apimanager
import net.eduard.api.lib.config.Config
import net.eduard.api.lib.database.DBManager
import net.eduard.api.lib.database.HybridTypes
import net.eduard.api.lib.database.SQLManager
import net.eduard.api.lib.hybrid.BungeeServer
import net.eduard.api.lib.hybrid.Hybrid
import net.eduard.api.lib.kotlin.resolve
import net.eduard.api.lib.kotlin.resolvePut
import net.eduard.api.lib.plugin.IPlugin
import java.io.File

val apiBungeeMain = resolve<BungeeMain>()

object BungeeMainKt : IPlugin {

    lateinit var manager: APIManager
    lateinit var config: Config

    init {
        Hybrid.instance = BungeeServer
    }

    fun onEnable() {
        val start = System.currentTimeMillis()

        APIJavaUtils.fastLog("§eIniciando carregamento...")
        resolvePut(this)
        HybridTypes // Carregamento de types 1

        APIJavaUtils.fastLog("§eCarregando diretórios...")
        // storage()
        config = Config(this, "config.yml")
        config.saveConfig()
        // reloadConfig() // x1
        // reloadConfig() // x2
        APIJavaUtils.fastLog("§eCarregando extras...")
        // reload()
        APIJavaUtils.fastLog("§eCarregando eventos...")
        // events()
        APIJavaUtils.fastLog("§eCarregando tasks...")
        // tasks()

        manager = resolvePut(APIManager())
        DBManager.setDebug(false)
        manager.sqlManager = SQLManager(SpigotMainKt.config["Database", DBManager::class.java])
        if (manager.sqlManager.dbManager.isEnabled) {
            APIJavaUtils.fastLog("§eEstabelecendo conexão com o MySQL...")
            apimanager.dbManager.openConnection()
            if (!apimanager.sqlManager.hasConnection()) error("Cannot connect to database")
            APIJavaUtils.fastLog("§aConexão estabelecida com o MySQL!")
        } else {
            APIJavaUtils.fastLog("")
            APIJavaUtils.fastLog("§cNão foi possível conectar ao MySQL. Alguns plugins e sistemas MK podem não funcionar corretamente.")
            APIJavaUtils.fastLog("")
        }


        val endTime = System.currentTimeMillis() - start
        APIJavaUtils.fastLog("§aPlugin ativado com sucesso! (Tempo levado: §f${endTime}ms§a)")
    }

    fun onDisable() {
        // onDisable aqui
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