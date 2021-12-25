package com.mikael.mkAPI.kotlin.utils

import org.bukkit.Bukkit

object APIUtils {

    fun broadcast(msg: String, notify: Boolean = false) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (notify) {
                player.notify()
            }
            player.sendMessage(msg)
        }
    }

}