package com.mikael.mkAPI.kotlin.api.bungee

import com.mikael.mkAPI.kotlin.api.redis.RedisAPI
import net.eduard.api.lib.modules.Mine

object BungeeAPI {

    fun isPlayerOnline(possiblePlayer: String): Boolean {
        return RedisAPI.client!!.exists("mkapi:bungee:players:${possiblePlayer.toLowerCase()}")
    }

    fun getPlayerServer(possiblePlayer: String): String {
        if (!isPlayerOnline(possiblePlayer)) return "null"
        return RedisAPI.client!!.get("mkapi:bungee:players:${possiblePlayer.toLowerCase()}")
    }

}