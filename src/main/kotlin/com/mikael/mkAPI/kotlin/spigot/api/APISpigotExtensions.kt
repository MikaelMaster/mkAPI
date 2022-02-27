package com.mikael.mkAPI.kotlin.spigot.api

import com.mikael.mkAPI.kotlin.api.APIManager
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import com.mikael.mkAPI.kotlin.utils.soundNo
import net.eduard.api.lib.kotlin.resolve
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

val apimanager = resolve<APIManager>()

/**
 * Adiciona um item ao inventário do player fornecido.
 * Caso o inventário esteja lotado, o item será dropado na location do player.
 *
 * @param item o ItemStack para ser adicionado ao inventário do player.
 * @return Um ItemStack dropado caso o inventário do player esteja cheio.
 */
fun Player.giveItem(item: ItemStack): Item? {
    val slot = this.inventory.withIndex().firstOrNull { it.value == null } ?: return this.world.dropItemNaturally(this.eyeLocation, item)
    this.inventory.setItem(slot.index, item)
    return null
}

/**
 * Executa a função fornecida (Unit) usando um Try Catch.
 * Em caso de erro, o mesmo será printado no console e uma mensagem será
 * enviada ao player dizendo que ocorreu um erro interno ao executar aquele comando.
 *
 * @param thing o bloco Unit para ser executado dentro do Try Catch.
 */
fun Player.runCommand(thing: () -> (Unit)) {
    try {
        thing.invoke()
    } catch (ex: Exception) {
        ex.printStackTrace()
        this.soundNo()
        this.sendMessage("§cAn internal error occurred while executing this command.")
    }
}

/**
 * Executa a função fornecida (Unit) usando um Try Catch em Async.
 * Em caso de erro, o mesmo será printado no console e uma mensagem será
 * enviada ao player dizendo que ocorreu um erro interno ao executar aquele comando.
 *
 * @param sendLoading se é para enviar uma mensagem de 'Carregando...' para o player ao executar o bloco Unit.
 * @param thing o bloco Unit para ser executado dentro do Try Catch em Async.
 */
fun Player.runCommandAsync(sendLoading: Boolean = false, thing: () -> (Unit)) {
    if (sendLoading) {
        this.sendMessage("§eLoading...")
    }
    SpigotMainKt.asyncTask {
        try {
            thing.invoke()
        } catch (ex: Exception) {
            ex.printStackTrace()
            this.soundNo()
            this.sendMessage("§cAn internal error occurred while executing this command.")
        }
    }
}

/**
 * Clears the player's inventory.
 */
fun Player.clearAllInventory() {
    this.inventory.clear()
    this.inventory.helmet = null
    this.inventory.chestplate = null
    this.inventory.leggings = null
    this.inventory.boots = null
}