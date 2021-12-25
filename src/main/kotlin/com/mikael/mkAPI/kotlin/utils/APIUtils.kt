package com.mikael.mkAPI.kotlin.utils

import org.bukkit.Bukkit

object APIUtils {

    fun broadcast(msg: String) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(msg)
        }
    }

}