package com.mikael.mkAPI.kotlin.spigot.task

import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import net.eduard.api.lib.manager.TimeManager
import net.eduard.api.lib.menu.Menu
import net.eduard.api.lib.menu.getMenu
import net.eduard.api.lib.modules.Mine

class AutoUpdateMenusTask : TimeManager(SpigotMainKt.config.getLong("MenuAPI.autoUpdateTicks")) {

    override fun run() {
        for (player in Mine.getPlayers()) {
            try {
                val menu = player.getMenu() ?: continue
                val pageOpened = menu.getPageOpen(player)
                val inventory = player.openInventory.topInventory
                menu.update(inventory, player, pageOpened, false)
                Menu
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}