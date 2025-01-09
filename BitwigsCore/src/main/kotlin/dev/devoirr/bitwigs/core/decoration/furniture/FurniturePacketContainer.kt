package dev.devoirr.bitwigs.core.decoration.furniture

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.util.Vector3f
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import dev.devoirr.bitwigs.core.toItemStack
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

class FurniturePacketContainer(
    id: Int,
    base64: String,
    location: Location,
    yaw: Float,
    furnitureType: FurnitureType
) {
    private val creationPacket = WrapperPlayServerSpawnEntity(
        id,
        UUID.randomUUID(),
        SpigotConversionUtil.fromBukkitEntityType(EntityType.ITEM_DISPLAY),
        SpigotConversionUtil.fromBukkitLocation(location.clone()),
        yaw,
        0,
        null
    )
    private val metadataPacket =
        WrapperPlayServerEntityMetadata(
            id,
            listOf(
                EntityData(
                    23,
                    EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(base64.toItemStack())
                ),
                EntityData(24, EntityDataTypes.BYTE, 8.toByte()),
                EntityData(
                    12,
                    EntityDataTypes.VECTOR3F,
                    Vector3f(
                        furnitureType.scale.width.toFloat(),
                        furnitureType.scale.height.toFloat(),
                        furnitureType.scale.depth.toFloat()
                    )
                )
            )
        )
    
    private val removalPacket = WrapperPlayServerDestroyEntities(id)
    fun sendRemovalForWorld(world: World) {
        world.players.forEach {
            PacketEvents.getAPI().playerManager.sendPacket(it, removalPacket)
        }
    }

    fun sendCreationForWorld(world: World) {
        world.players.forEach {
            sendCreationForPlayer(it)
        }
    }

    fun sendCreationForPlayer(player: Player) {
        PacketEvents.getAPI().playerManager.sendPacket(player, creationPacket)
        PacketEvents.getAPI().playerManager.sendPacket(player, metadataPacket)
    }
}