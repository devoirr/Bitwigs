package dev.devoirr.bitwigs.core.frames

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.entity.data.EntityData
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import dev.devoirr.bitwigs.core.toBase64
import dev.devoirr.bitwigs.core.toItemStack
import dev.devoirr.bitwigs.core.toLocation
import io.github.retrooper.packetevents.util.SpigotConversionUtil
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class PacketFrame(
    val id: Int,
    val location: Location,
    val blockFace: BlockFace,
    var rotation: Int,
    var itemStack: ItemStack?
) {

    private val creationPacket =
        WrapperPlayServerSpawnEntity(
            id,
            null,
            EntityTypes.ITEM_FRAME,
            SpigotConversionUtil.fromBukkitLocation(location.clone()),
            0f,
            blockFace.toFrameData(),
            null
        )

    private val removalPacket = WrapperPlayServerDestroyEntities(id)

    private var metadataPacket =
        WrapperPlayServerEntityMetadata(
            id,
            listOf(
                EntityData(8, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(itemStack)),
                EntityData(9, EntityDataTypes.INT, rotation)
            )
        )

    fun rotate(rotation: Int) {
        this.rotation = rotation
        this.recreateMetadata()

        location.getNearbyPlayers(16.0).forEach {
            PacketEvents.getAPI().playerManager.sendPacket(it, metadataPacket)
        }
    }

    fun changeItemStack(itemStack: ItemStack) {
        this.itemStack = itemStack
        this.recreateMetadata()

        location.getNearbyPlayers(16.0).forEach {
            PacketEvents.getAPI().playerManager.sendPacket(it, metadataPacket)
        }
    }

    fun delete() {
        location.getNearbyPlayers(16.0).forEach {
            PacketEvents.getAPI().playerManager.sendPacket(it, removalPacket)
        }
    }

    fun create() {
        location.getNearbyPlayers(16.0).forEach {
            PacketEvents.getAPI().playerManager.sendPacket(it, creationPacket)
            PacketEvents.getAPI().playerManager.sendPacket(it, metadataPacket)
        }
    }

    fun createFor(player: Player) {
        player.let {
            PacketEvents.getAPI().playerManager.sendPacket(it, creationPacket)
            PacketEvents.getAPI().playerManager.sendPacket(it, metadataPacket)
        }
    }

    private fun recreateMetadata() {
        this.metadataPacket = WrapperPlayServerEntityMetadata(
            id,
            listOf(EntityData(8, EntityDataTypes.ITEMSTACK, itemStack), EntityData(9, EntityDataTypes.INT, rotation))
        )
    }

    fun toRow(): PacketFrameRow {
        val row = PacketFrameRow()

        row.id = id
        row.location = location.toString()
        row.blockFace = blockFace.name
        row.rotation = rotation
        row.itemStack = itemStack?.toBase64()

        return row
    }

    companion object {
        fun fromRow(row: PacketFrameRow): PacketFrame {
            val blockFace = BlockFace.valueOf(row.blockFace)
            val location = row.location.toLocation()
            val itemStack = row.itemStack?.toItemStack()

            return PacketFrame(row.id, location, blockFace, row.rotation, itemStack)
        }
    }
}

@DatabaseTable(tableName = "frames")
class PacketFrameRow {

    @DatabaseField(id = true, canBeNull = false, dataType = DataType.INTEGER)
    var id: Int = 0

    @DatabaseField(canBeNull = false)
    lateinit var location: String

    @DatabaseField(canBeNull = false)
    lateinit var blockFace: String

    @DatabaseField(canBeNull = false)
    var rotation: Int = 0

    @DatabaseField(canBeNull = true)
    var itemStack: String? = null
}

fun BlockFace.toFrameData(): Int {
    return when (this) {
        BlockFace.UP -> 0
        BlockFace.DOWN -> 1
        BlockFace.NORTH -> 3
        BlockFace.SOUTH -> 4
        BlockFace.WEST -> 5
        BlockFace.EAST -> 6
        else -> 0
    }
}