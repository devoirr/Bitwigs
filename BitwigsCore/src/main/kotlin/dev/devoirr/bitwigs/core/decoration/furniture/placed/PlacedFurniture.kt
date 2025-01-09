package dev.devoirr.bitwigs.core.decoration.furniture.placed

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.decoration.furniture.FurniturePacketContainer
import dev.devoirr.bitwigs.core.toItemStack
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable

data class PlacedFurniture(
    private val manager: FurnitureManager,
    val type: String,
    val center: Block,
    val facing: BlockFace,
    val yaw: Float,
    val display: Int,
    val base64: String,
    val seats: MutableList<ArmorStand> = mutableListOf()
) {

    /* Used to manage packets */
    private val packetContainer: FurniturePacketContainer

    init {
        val location = center.location.clone().add(0.5, 0.5, 0.5)
        location.yaw = yaw
        location.pitch = 0f
        packetContainer = FurniturePacketContainer(
            display,
            base64,
            location,
            yaw,
            manager.getFurnitureType(type)!!
        )
    }

    /**
     * Destroys the furniture completely (inc. database)
     */
    fun destroy(manager: FurnitureManager) {
        manager.getFurnitureType(type)?.let {
            center.world.dropItem(center.location.clone().add(0.5, 0.5, 0.5), base64.toItemStack())
        }
        sendRemovalForWorld()
        val blocks = manager.getFurnitureType(type)?.hitbox?.getBlocks(center, facing, true) ?: return
        blocks.forEach {
            it.type = Material.AIR
            it.removeMetadata("furniture", BitwigsPlugin.instance)
        }
        destroySeats()
    }

    /**
     * Destroys all the existing seats by removing the armorstands, and clears the list.
     */
    private fun destroySeats() {
        for (seat in seats) {
            seat.remove()
        }
        seats.clear()
    }

    fun addSeat(armorStand: ArmorStand) {
        seats.add(armorStand)
    }

    /**
     * Updates metadata for each block.
     */
    fun updateMetadata(manager: FurnitureManager) {
        val blocks = manager.getFurnitureType(type)?.hitbox?.getBlocks(center, facing, true) ?: return
        blocks.forEach {
            it.type = Material.BARRIER
            it.setMetadata("furniture", FixedMetadataValue(BitwigsPlugin.instance, display))
        }
    }

    /**
     * Sends the removalPacket to each player inside the world
     */
    private fun sendRemovalForWorld() {
        packetContainer.sendRemovalForWorld(center.world)
    }

    /**
     * Sends the creationPacket to each player inside the world
     */
    fun sendCreationForWorld() {
        object : BukkitRunnable() {
            override fun run() {
                packetContainer.sendCreationForWorld(center.world)
            }
        }.runTaskLaterAsynchronously(BitwigsPlugin.instance, 5L)
    }

    /**
     * Sends the creationPacket to specific player.
     */
    fun sendCreationForPlayer(player: Player) {
        packetContainer.sendCreationForPlayer(player)
    }

}