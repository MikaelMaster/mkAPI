package com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3

import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

interface PlayerNPC {

    fun getPlayer(): Player
    fun isSpawned(): Boolean
    fun spawn(location: Location)
    fun setSkin(skinName: String)
    fun setSkin(skinValue: String, skinSignature: String)
    fun despawn()
    fun teleport(location: Location)
    fun showFor(player: Player)
    fun hideFor(player: Player)
    fun respawn()
    fun getSpawnLocation(): Location
    fun setName(name: String)
    fun getId(): UUID

}