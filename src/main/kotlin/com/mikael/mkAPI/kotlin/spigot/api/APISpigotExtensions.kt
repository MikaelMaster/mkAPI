package com.mikael.mkAPI.kotlin.spigot.api

import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import com.mikael.mkAPI.kotlin.utils.soundNo
import net.eduard.api.lib.kotlin.resolve
import org.bukkit.entity.Player

val apimanager = resolve<APIManager>()

fun Player.runCommand(thing: () -> (Unit)) {
    try {
        thing.invoke()
    } catch (ex: Exception) {
        ex.printStackTrace()
        this.soundNo()
        this.sendMessage("§cOcorreu um erro interno ao executar este comando.")
    }
}

fun Player.runCommandAsync(sendLoading: Boolean = false, thing: () -> (Unit)) {
    if (sendLoading) {
        this.sendMessage("§eCarregando...")
    }
    SpigotMainKt.asyncTask {
        try {
            thing.invoke()
        } catch (ex: Exception) {
            ex.printStackTrace()
            this.soundNo()
            this.sendMessage("§cOcorreu um erro interno ao executar este comando.")
        }
    }
}

fun Player.clearAllInventory() {
    this.inventory.clear()
    this.inventory.helmet = null
    this.inventory.chestplate = null
    this.inventory.leggings = null
    this.inventory.boots = null
}