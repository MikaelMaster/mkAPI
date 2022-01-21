package com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3

import net.eduard.api.server.EduardPlugin
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

object PlayerNpcAPI {
    lateinit var plugin: EduardPlugin
    private val npcs = mutableMapOf<UUID, PlayerNPC>()
    fun create(name: String, world: World): PlayerNPC {
        val playerNPC = PlayerNPC_1_8_9(name, world)
        npcs[playerNPC.npcId] = playerNPC
        return playerNPC;
    }

    fun create(world: World): PlayerNPC {
        return create("§8[NPC]", world)
    }

    fun isNPC(player: Player): Boolean {
        return player.uniqueId in npcs
    }

    fun delete(npc: PlayerNPC) {
        npc.despawn()
        npcs.remove(npc.getId())
    }

}