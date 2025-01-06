package dev.devoirr.bitwigs.core.block.furniture.model.placed

import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.block.furniture.model.FurnitureType
import dev.devoirr.bitwigs.core.toItemStack
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Light
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
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

    companion object {

        /* Used for placing light blocks */
        val BLOCK_FACES = mutableListOf(
            BlockFace.SELF,
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.SOUTH
        )

    }

    /* Used to manage packets */
    private val packetContainer: dev.devoirr.bitwigs.core.block.furniture.model.FurniturePacketContainer

    init {
        val location = center.location.clone().add(0.5, 0.5, 0.5)

        location.yaw = yaw
        location.pitch = 0f

        packetContainer = dev.devoirr.bitwigs.core.block.furniture.model.FurniturePacketContainer(
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
            if (!it.cancelDrop)
                center.world.dropItem(center.location.clone().add(0.5, 0.5, 0.5), base64.toItemStack())

            removeLight(it)
        }

        sendRemovalForWorld()

        val blocks = manager.getFurnitureType(type)?.hitbox?.getBlocks(center, facing, true) ?: return
        blocks.forEach {
            it.type = Material.AIR
            manager.metadataManager.removeFromBlock("furniture", it)
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
            it.setMetadata("furniture", manager.metadataManager.createMetadataValue(display.toString()))
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
        manager.getTaskManager().runTaskLaterAsync(object : BukkitRunnable() {
            override fun run() {
                packetContainer.sendCreationForWorld(center.world)
            }
        }, 5L)
    }

    /**
     * Sends the creationPacket to specific player.
     */
    fun sendCreationForPlayer(player: Player) {
        packetContainer.sendCreationForPlayer(player)
    }

    /**
     * Creates light blocks around the furniture.
     */
    fun setLight(furnitureType: FurnitureType) {
        val level = furnitureType.light.coerceAtMost(15)
        if (level <= 0)
            return

        val lightData = Material.LIGHT.createBlockData() as Light
        lightData.level = level

        var block: Block
        for (blockFace in BLOCK_FACES) {
            block = center.getRelative(blockFace)

            if (block.type != Material.LIGHT && block.type != Material.AIR)
                continue

            if (block.blockData is Light && (block.blockData as Light).level > level)
                continue

            block.blockData = lightData
        }
    }

    /**
     * Removes light blocks around the furniture.
     */
    private fun removeLight(furnitureType: FurnitureType) {
        val level = furnitureType.light.coerceAtMost(15)
        if (level <= 0)
            return

        var block: Block
        for (blockFace in BLOCK_FACES) {
            block = center.getRelative(blockFace)
            if (block.type != Material.LIGHT && block.type != Material.AIR)
                continue

            if (block.blockData is Light && (block.blockData as Light).level > level)
                continue

            block.type = Material.AIR
        }
    }

}

