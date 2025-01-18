package dev.devoirr.bitwigs.core.decoration.furniture.entities

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import dev.devoirr.bitwigs.core.decoration.Hitbox
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object FurnitureEntityManager {

    class FurnitureEntity(val id: Int, val itemStack: ItemStack, val location: Location, scale: Hitbox) {
        val creationPacket: WrapperPlayServerSpawnEntity = WrapperPlayServerSpawnEntity(
            id,
            UUID.randomUUID(),
            SpigotConversionUtil.fromBukkitEntityType(EntityType.ITEM_DISPLAY),
            SpigotConversionUtil.fromBukkitLocation(location.clone()),
            location.yaw,
            0,
            null
        )

        val metaDataPacket: WrapperPlayServerEntityMetadata = WrapperPlayServerEntityMetadata(
            id,
            listOf(
                EntityData(
                    23,
                    EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(itemStack)
                ),
                EntityData(24, EntityDataTypes.BYTE, 8.toByte()),
                EntityData(
                    12,
                    EntityDataTypes.VECTOR3F,
                    Vector3f(
                        scale.width.toFloat(),
                        scale.height.toFloat(),
                        scale.depth.toFloat()
                    )
                )
            )
        )
    }

    /* placed_id -> placed_entity */
    private val map = mutableMapOf<String, FurnitureEntity>()

    fun register(type: String, entity: FurnitureEntity) {
        map[type] = entity
    }

    fun delete(key: String) {
        val entity = map.remove(key) ?: return

        val removalPacket = WrapperPlayServerDestroyEntities(entity.id)
        for (player in entity.location.getNearbyPlayers(16.0)) {
            PacketEvents.getAPI().playerManager.sendPacket(player, removalPacket)
        }
    }

    fun sendToEveryone(key: String) {
        val entity = map[key] ?: return

        entity.location.getNearbyPlayers(16.0).forEach {
            sendEntityToPlayer(entity, it)
        }
    }


    fun sendAllToPlayer(player: Player) {

        val entities = map.values.filter { it.location.world.name == player.world.name }
        entities.forEach { sendEntityToPlayer(it, player) }
    }

    private fun sendEntityToPlayer(entity: FurnitureEntity, player: Player) {
        PacketEvents.getAPI().playerManager.sendPacket(player, entity.creationPacket)
        PacketEvents.getAPI().playerManager.sendPacket(player, entity.metaDataPacket)
    }
}