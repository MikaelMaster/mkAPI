package com.mikael.mkAPI.kotlin.spigot

import com.mikael.mkAPI.java.APIJavaUtils
import com.mikael.mkAPI.java.spigot.SpigotMain
import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.api.redis.RedisAPI
import com.mikael.mkAPI.kotlin.api.redis.RedisConnectionData
import com.mikael.mkAPI.kotlin.objects.MinigameProfile
import com.mikael.mkAPI.kotlin.objects.SpigotServerData
import com.mikael.mkAPI.kotlin.spigot.api.apimanager
import com.mikael.mkAPI.kotlin.spigot.api.hasVaultEconomy
import com.mikael.mkAPI.kotlin.spigot.task.AutoUpdateMenusTask
import com.mikael.mkAPI.kotlin.spigot.task.PlayerTargetAtPlayerTask
import com.mikael.mkAPI.kotlin.spigot.listener.VersionCommandListener
import com.mikael.mkAPI.kotlin.spigot.listener.MinigameAPIListener
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
import net.eduard.api.lib.plugin.IPluginInstance
import net.eduard.api.lib.score.DisplayBoard
import net.eduard.api.lib.storage.StorageAPI
import net.eduard.api.lib.storage.storables.BukkitStorables
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.concurrent.thread

object SpigotMainKt : IPluginInstance, BukkitTimeHandler {

    lateinit var manager: APIManager
    lateinit var config: Config

    init {
        Hybrid.instance = BukkitServer
    }

    fun onEnable() {
        val start = System.currentTimeMillis()

        APIJavaUtils.fastLog("§eIniciando carregamento...")
        HybridTypes // Carregamento de types 1
        BukkitTypes.register() // Carregamento de types 2
        store<RedisConnectionData>()

        APIJavaUtils.fastLog("§eCarregando diretórios...")
        storage()
        config = Config(SpigotMain.getPlugin(SpigotMain::class.java), "config.yml")
        config.saveConfig()
        reloadConfig() // x1
        reloadConfig() // x2
        StorageAPI.updateReferences()
        APIJavaUtils.fastLog("§eCarregando extras...")
        reload()
        APIJavaUtils.fastLog("§eCarregando tasks...")
        tasks()

        APIJavaUtils.fastLog("§eCarregando Vault...")
        if (!hasVaultEconomy()) {
            APIJavaUtils.fastLog("§cNão foi possível encontrar o plugin 'Vault' ou não foi possível carregar sua economia. Alguns plugins MK podem não funcionar corretamente.")
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
            APIJavaUtils.fastLog("§eEstabelecendo conexão com o MySQL...")
            apimanager.dbManager.openConnection()
            if (!apimanager.sqlManager.hasConnection()) error("Cannot connect to database server")
            APIJavaUtils.fastLog("§aConexão estabelecida com o MySQL!")
        } else {
            APIJavaUtils.fastLog("§cO MySQL não está ativo na Config. Alguns plugins e sistemas MK podem não funcionar corretamente.")
        }

        RedisAPI.managerData = config["Redis", RedisConnectionData::class.java]
        if (RedisAPI.managerData.isEnabled) {
            APIJavaUtils.fastLog("§eEstabelecendo conexão com servidor Redis...")
            RedisAPI.createClient(RedisAPI.managerData)
            RedisAPI.connectClient()
            if (RedisAPI.client == null || RedisAPI.clientConnection == null) error("Cannot connect to Redis server")
            APIJavaUtils.fastLog("§aConexão estabelecida com o servidor Redis!")
        } else {
            APIJavaUtils.fastLog("§cO Redis não está ativo na Config. Alguns plugins e sistemas MK podem não funcionar corretamente.")
        }

        if (config.getBoolean("BungeeAPI.isEnable")) {
            APIJavaUtils.fastLog("§eCarregando BungeeAPI...")
            bungeeAPI()
        }

        if (config.getBoolean("enable-minigameAPI")) {
            APIJavaUtils.fastLog("§eCarregando MinigameAPI...")
            minigameAPI()
        }

        APIJavaUtils.fastLog("§eCarregando sistemas...")
        ServerBusyListener().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))
        VersionCommandListener().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))
        NPCGeneralListener().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))
        InvunerableEntitySystem().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))

        val endTime = System.currentTimeMillis() - start
        APIJavaUtils.fastLog("§aPlugin ativado com sucesso! (Tempo levado: §f${endTime}ms§a)")

        syncDelay(20) {
            SpigotMain.serverEnabled = true
            APIJavaUtils.fastLog("§aO servidor foi marcado como disponível!")

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
    }

    fun onDisable() {
        if (config.getBoolean("CustomKick.enable-custom-kick")) {
            APIJavaUtils.fastLog("§eDesconectando jogadores...")
            for (playerLoop in Bukkit.getOnlinePlayers()) {
                playerLoop.kickPlayer(config.getString("CustomKick.custom-kick-message"))
            }
        }
        APIJavaUtils.fastLog("§eDescarregando sistemas...")
        BungeeAPI.controller.unregister()
        apimanager.dbManager.closeConnection()
        RedisAPI.finishConnection()
        APIJavaUtils.fastLog("§cPlugin desativado!")
    }

    private fun bungeeAPI() {
        if (apimanager.sqlManager.hasConnection()) {
            SpigotMain.mkBungeeAPIEnabled = true
            apimanager.sqlManager.createTable<SpigotServerData>()
            apimanager.sqlManager.createReferences<SpigotServerData>()
        } else {
            APIJavaUtils.fastLog("")
            APIJavaUtils.fastLog("§cNão foi possível carregar o BungeeAPI. Ative o MySQL na config do mkAPI e religue o servidor.")
            APIJavaUtils.fastLog("")
        }
    }

    private fun minigameAPI() {
        if (apimanager.sqlManager.hasConnection()) {
            SpigotMain.mkMinigameAPIEnabled = true
            apimanager.sqlManager.createTable<MinigameProfile>()
            apimanager.sqlManager.createReferences<MinigameProfile>()
            MinigameAPIListener().registerListener(SpigotMain.getPlugin(SpigotMain::class.java))
        } else {
            APIJavaUtils.fastLog("")
            APIJavaUtils.fastLog("§cNão foi possível carregar o MinigameAPI. Ative o MySQL na config do mkAPI e religue o servidor.")
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
        if (config.getBoolean("MenuAPI.auto-update-menus")) {
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
        config.add(
            "MenuAPI.auto-update-menus",
            true,
            "Se é pra atualizar os menus abertos.",
            "ATENÇÃO: Para servidores de Rank UP e Factions é aconselhável deixar o auto update DESATIVADO."
        )
        config.add(
            "MenuAPI.auto-update-ticks",
            60,
            "Tempo para atualizar menus abertos de jogadores.",
            "Valores menores de 20 irão causar lag no servidor."
        )
        config.add(
            "BungeeAPI.isEnable", false, "Se é para ativar o BungeeAPI do mkAPI.",
            "Plugins MK que trabalham com bungee (mkLobby) precisam dessa função ativa."
        )
        config.add(
            "BungeeAPI.currentServerName", "server-name", "O nome deste servidor spigot perante o bungee.",
            "Coloque o nome do servidor como ele está na config do bungee."
        )
        config.add(
            "BungeeAPI.currentServerMaxAmount",
            100,
            "Quantidade máxima de jogadores deste servidor spigot perante o bungee.",
        )
        config.add(
            "enable-minigameAPI", false, "Se é para ativar o MinigameAPI do mkAPI.",
            "Caso você esteja utilizando um plugin MK de minigame (mkBattleRoyale) em seu servidor, habilite essa função."
        )
        config.add(
            "CustomKick.enable-custom-kick",
            true,
            "Se é para kickar os jogadores no desligamento do servidor."
        )
        config.add(
            "CustomKick.custom-kick-message",
            "§cReiniciando, voltamos em breve!",
            "Mensagem de kick enviada aos jogadores no desligamento do servidor."
        )
        config.saveConfig()
    }

    override fun getPlugin(): Any {
        return SpigotMain.getPlugin(SpigotMain::class.java)
    }

    override fun getSystemName(): String {
        return SpigotMain.getPlugin(SpigotMain::class.java).name
    }

    override fun getPluginFolder(): File {
        return SpigotMain.getPlugin(SpigotMain::class.java).dataFolder
    }

    override fun getPluginConnected(): Plugin {
        return SpigotMain.getPlugin(SpigotMain::class.java)
    }
}