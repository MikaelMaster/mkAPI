package com.mikael.mkAPI.kotlin.api

import net.eduard.api.lib.kotlin.clearInventory
import net.eduard.api.lib.kotlin.resolve
import org.bukkit.entity.Player

val apimanager = resolve<APIManager>()

fun Player.runCommand(thing: () -> (Unit)) {
    try {
        thing.invoke()
    } catch (ex: Exception) {
        ex.printStackTrace()
        this.sendMessage("§cOcorreu um erro interno ao executar este comando.")
    }
}

fun Player.clearAllInventory() {
    this.inventory.clear()
    this.inventory.helmet = null
    this.inventory.chestplate = null
    this.inventory.leggings = null
    this.inventory.boots = null
}