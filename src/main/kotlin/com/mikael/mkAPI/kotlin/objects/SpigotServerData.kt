package com.mikael.mkAPI.kotlin.objects

import com.mikael.mkAPI.kotlin.api.MKPluginData
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import com.mikael.mkAPI.kotlin.spigot.api.apimanager
import net.eduard.api.lib.database.annotations.ColumnPrimary
import net.eduard.api.lib.database.annotations.ColumnUnique
import net.eduard.api.lib.database.annotations.TableName

@TableName("mkapi_spigot_servers")
class SpigotServerData : MKPluginData {

    @ColumnPrimary
    var id = 0L

    @ColumnUnique
    var server = "server-name"

    var isOnline = false

    var playerAmount = 0

    var maxPlayerAmount = 100

    companion object {
        lateinit var currentServer: SpigotServerData
        val currentServerName = SpigotMainKt.config.getString("BungeeAPI.currentServerName")
        val currentServerMaxAmount = SpigotMainKt.config.getInt("BungeeAPI.currentServerMaxAmount")

        fun setupMySQL() {
            val data = apimanager.sqlManager.getData(
                SpigotServerData::class.java,
                "server",
                currentServerName
            )
            if (data != null) return
            val newData = SpigotServerData()
            newData.server = currentServerName
            newData.maxPlayerAmount = currentServerMaxAmount
            newData.insert()
        }

        fun updateRam(): SpigotServerData {
            currentServer = apimanager.sqlManager.getData(SpigotServerData::class.java, "server", currentServerName)!!
            return currentServer
        }

        val currentServerCache
            get(): SpigotServerData? {
                if (!this::currentServer.isInitialized) return null
                return currentServer
            }
    }

}