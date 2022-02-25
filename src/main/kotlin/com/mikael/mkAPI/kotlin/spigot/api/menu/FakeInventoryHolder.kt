package com.mikael.mkAPI.kotlin.spigot.api.menu

import com.mikael.mkAPI.kotlin.spigot.api.plugin.MineMenu
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class FakeInventoryHolder(val menu: MineMenu) : InventoryHolder {
    lateinit var openInventory: Inventory
    var pageOpenned = 1

    override fun getInventory(): Inventory {
        return openInventory
    }

}