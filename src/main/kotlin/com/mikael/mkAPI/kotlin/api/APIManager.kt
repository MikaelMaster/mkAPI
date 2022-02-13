package com.mikael.mkAPI.kotlin.api

import com.mikael.mkAPI.kotlin.objects.MinigameProfile
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import com.mikael.mkAPI.kotlin.spigot.api.apimanager
import net.eduard.api.lib.database.SQLManager
import net.eduard.api.lib.hybrid.PlayerUser
import net.eduard.api.lib.kotlin.mineSendActionBar
import net.eduard.api.lib.kotlin.offline
import org.bukkit.entity.Player
import java.util.*

val minigameProfiles = mutableMapOf<PlayerUser, MinigameProfile>()
val minigameProfileSyncKey = Any()

val bungeeAPISyncKey = Any()

fun PlayerUser.startUserOrUpdate(): MinigameProfile {
    return startMinigameUserOrUpdateReal(this)
}

fun PlayerUser.minigameUser(): MinigameProfile {
    return minigameProfiles[this]!!
}

fun Player.minigameUser(): MinigameProfile {
    return minigameProfiles[this.offline]!!
}

fun startMinigameUserOrUpdateReal(player: PlayerUser): MinigameProfile {
    var user = apimanager.sqlManager.getData(
        MinigameProfile::class.java, "player", player
    )
    if (user == null) {
        user = MinigameProfile()
        user.player = player
        user.insert()
    }
    minigameProfiles[player] = user
    return user
}

fun startMinigameUserOrUpdate(id: Int): MinigameProfile {
    val user = apimanager.sqlManager.getData(MinigameProfile::class.java, id)!!
    val player = user.player
    return startMinigameUserOrUpdateReal(player)
}

class APIManager {
    companion object {
        lateinit var instance: APIManager
    }

    init {
        instance = this@APIManager
    }

    val dbManager get() = sqlManager.dbManager
    lateinit var sqlManager: SQLManager

    fun hasUserInDatabase(player: PlayerUser): Boolean {
        val query = dbManager.select("select id from mkapi_minigame_players where player like '${player.name};%'")!!
        var hasUser = false
        try {
            if (query.next()) {
                val id = query.getInt("id")
                startMinigameUserOrUpdate(id)
                hasUser = true
            }
        } finally {
            query.statement.close()
            query.close()
        }
        return hasUser
    }
}