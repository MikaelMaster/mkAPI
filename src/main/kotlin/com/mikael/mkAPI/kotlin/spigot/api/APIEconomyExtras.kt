package com.mikael.mkAPI.kotlin.spigot.api

import net.eduard.api.lib.modules.VaultAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun hasVaultEconomy(): Boolean {
    return !(Bukkit.getPluginManager().getPlugin("Vault") == null ||
            !VaultAPI.hasEconomy())
}

fun Player.addMoney(amount: Double, world: String? = null) {
    if (Bukkit.getPluginManager().getPlugin("Vault") == null ||
        !VaultAPI.hasEconomy()
    ) error("Cannot get Vault Economy")
    VaultAPI.getEconomy().depositPlayer(this, world, amount)
}

fun Player.removeMoney(amount: Double, world: String? = null) {
    if (Bukkit.getPluginManager().getPlugin("Vault") == null ||
        !VaultAPI.hasEconomy()
    ) error("Cannot get Vault Economy")
    VaultAPI.getEconomy().withdrawPlayer(this, world, amount)
}