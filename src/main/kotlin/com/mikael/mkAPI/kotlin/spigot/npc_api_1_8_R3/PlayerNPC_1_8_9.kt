package com.mikael.mkAPI.kotlin.spigot.npc_api_1_8_R3

import com.mikael.mkAPI.java.spigot.SpigotMain
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt
import com.mikael.mkAPI.kotlin.spigot.SpigotMainKt.asyncDelay
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.eduard.api.lib.kotlin.cut
import net.eduard.api.lib.modules.Extra
import net.eduard.api.lib.modules.FakePlayer
import net.eduard.api.lib.modules.Mine
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_8_R3.CraftServer
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

class PlayerNPC_1_8_9(
    val npcName: String,
    val npcWorld: World
) : PlayerNPC {
    val nmsWorld = (npcWorld as CraftWorld).handle
    val nmsServer = (Bukkit.getServer() as CraftServer).handle
    val npcInteractManager = PlayerInteractManager(nmsWorld)
    val npcId = FakePlayer(Extra.newKey(Extra.KeyType.LETTER, 16)).uniqueId
    val npcProfile = GameProfile(npcId, npcName.cut(16))
    val npc = EntityPlayer(nmsServer.server, nmsWorld, npcProfile, npcInteractManager)
    var npcLocation = npcWorld.spawnLocation!!

    init {
        npc.playerConnection = PlayerConnection_1_8_9(npc);

        // define que para aparecer todas as partes da skin e capa
        npc.dataWatcher.watch(10, 127.toByte())

    }

    override fun getPlayer(): Player {
        return npc.bukkitEntity
    }

    override fun isSpawned(): Boolean {
        return !npc.bukkitEntity.isDead
    }

    override fun teleport(location: Location) {

    }

    override fun spawn(location: Location) {
        this.npcLocation = location
        //PlayerNpcAPI.plugin.log("Spawnando npc")
        npc.pitch = npcLocation.pitch
        npc.yaw = npcLocation.yaw
        //definir head rotation
        npc.f(npcLocation.yaw)
        npc.setLocation(npcLocation.x, npcLocation.y, npcLocation.z, npcLocation.yaw, npcLocation.pitch)
        /*
        if (entity is EntityHuman) {
            val entityhuman = entity as EntityHuman
            this.players.add(entityhuman)
            this.everyoneSleeping()
        }
        this.getChunkAt(i, j).a(entity)
        this.entityList.add(entity)
        this.a(entity)
        */
        //val i = MathHelper.floor(npc.locX / 16.0)
        //val j = MathHelper.floor(npc.locZ / 16.0)
        // nmsWorld.entityList.add(npc)
        //nmsWorld.a(npc)
        // nmsWorld.getChunkAt(i, j).a(npc)
        /*
        this.entitiesById.a(entity.getId(), entity)
        this.entitiesByUUID.put(entity.getUniqueID(), entity)
        val aentity: Array<Entity> = entity.aB()
        if (aentity != null) {
            for (i in aentity.indices) {
                this.entitiesById.a(aentity[i].id, aentity[i])
            }
        }

        */
        nmsWorld.addEntity(npc, CreatureSpawnEvent.SpawnReason.CUSTOM)
        npc.bukkitEntity.setMetadata("NPC", FixedMetadataValue(SpigotMain.getPlugin(SpigotMain::class.java), true))
        // npc.listName = null
        for (player in Mine.getPlayers()) {
            if (player == getPlayer()) continue
            hideFor(player)
            showFor(player)
        }
        //PlayerNpcAPI.plugin.log("Spawnando npc finalizado")
    }

    override fun setSkin(skinValue: String, skinSignature: String) {
        val playerProfile = npcProfile
        //  playerProfile.properties.clear()
        try {
            playerProfile.properties.put(
                "textures",
                Property("textures", skinValue, skinSignature)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun despawn() {
        nmsWorld.removeEntity(npc)
        for (player in Mine.getPlayers()) {
            hideFor(player)
        }
    }

    override fun setSkin(skinName: String) {
        val playerProfile: GameProfile = npcProfile
        playerProfile.properties.clear()
        val uuid = Extra.getPlayerUUIDByName(skinName)
        val skinProperty = Extra.getSkinProperty(uuid)
        val name = skinProperty.get("name").asString
        val value = skinProperty.get("value").asString
        val signature: String = skinProperty.get("signature").asString
        setSkin(value, signature)
    }

    override fun showFor(player: Player) {
        val packetPlayerSpawn = PacketPlayOutNamedEntitySpawn(npc)
        val packetPlayerInfoAdd = PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
            npc
        )
        val packetPlayerUpdateMetadata = PacketPlayOutEntityMetadata(
            npc.id,
            npc.dataWatcher, true
        )
        val packetPlayerHeadRotation = PacketPlayOutEntityHeadRotation(
            npc,
            MathHelper.d(npc.headRotation * 256.0f / 360.0f).toByte()
        )
        val playerConnection = (player as CraftPlayer).handle.playerConnection
        playerConnection.sendPacket(packetPlayerInfoAdd)
        playerConnection.sendPacket(packetPlayerUpdateMetadata)
        playerConnection.sendPacket(packetPlayerSpawn)
        playerConnection.sendPacket(packetPlayerHeadRotation)
        // quando vc quer q o nome do npc fake saia do tab precisa remover ele

        SpigotMainKt.asyncDelay(20) {
            val packetPlayerInfoRemove = PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                npc
            )
            playerConnection.sendPacket(packetPlayerInfoRemove)
        }

    }

    override fun hideFor(player: Player) {
        val packetDestroy = PacketPlayOutEntityDestroy(npc.id)
        val packetPlayerInfoRemove = PacketPlayOutPlayerInfo(
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
            npc
        )
        val playerConnection = (player as CraftPlayer).handle.playerConnection
        playerConnection.sendPacket(packetDestroy)
        playerConnection.sendPacket(packetPlayerInfoRemove)
    }

    override fun respawn() {
        for (player in Mine.getPlayers()) {
            hideFor(player)
            showFor(player)
        }
    }

    override fun getSpawnLocation(): Location {
        return npcLocation
    }


    override fun setName(name: String) {

    }

    override fun getId(): UUID {
        return npcId
    }
}