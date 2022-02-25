package com.mikael.mkAPI.kotlin.spigot.api

import net.eduard.api.lib.modules.VaultAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Verifica se o plugin 'Vault' está no servidor.
 *
 * @return True se o plugin Vault estiver no servidor. Senão, false.
 */
fun hasVaultPlugin(): Boolean {
    return Bukkit.getPluginManager().getPlugin("Vault") != null
}

/**
 * Verifica se o plugin 'Vault' está no servidor e se ele possui uma economia registrada.
 *
 * @return True se ele tiver um economia registrada. Senão, false.
 */
fun hasVaultEconomy(): Boolean {
    if (!hasVaultPlugin()) return false
    return VaultAPI.hasEconomy()
}

/**
 * Adiciona money para um player utilizando o Vault Economy.
 *
 * @throws IllegalStateException se o Vault Economy for null.
 * @see hasVaultEconomy
 */
fun Player.addMoney(amount: Double, world: String? = null) {
    if (!hasVaultPlugin() || !VaultAPI.hasEconomy()) error("Cannot get Vault Economy")
    VaultAPI.getEconomy().depositPlayer(this, world, amount)
}

/**
 * Remove money de um player utilizando o Vault Economy.
 *
 * @throws IllegalStateException se o Vault Economy for null.
 * @see hasVaultEconomy
 */
fun Player.removeMoney(amount: Double, world: String? = null) {
    if (!hasVaultPlugin() || !VaultAPI.hasEconomy()) error("Cannot get Vault Economy")
    VaultAPI.getEconomy().withdrawPlayer(this, world, amount)
}