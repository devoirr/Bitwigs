package dev.devoirr.bitwigs.core.decoration.furniture.listener

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.listener.Listener
import dev.devoirr.bitwigs.core.util.PlayerUtility
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

class FurniturePlaceListener(private val manager: FurnitureManager) : Listener() {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun onClick(event: PlayerInteractEvent) {
        if (!event.action.isRightClick)
            return
        if (!event.hasItem() || !event.hasBlock())
            return

        val player = event.player
        val item = event.item ?: return
        val block = event.clickedBlock ?: return
        val blockFace = event.blockFace
        val furnitureType = manager.getFurnitureType(item) ?: return

        event.isCancelled = true

        val target = if (block.isReplaceable) block else block.getRelative(blockFace)

        val playerBlockFace = PlayerUtility.yawToBlockFace(player)
        val blocks = furnitureType.hitbox.getBlocks(target, playerBlockFace)

        if (blocks.any { !it.isReplaceable }) {
            event.isCancelled = true
            player.playSound(player, Sound.ENTITY_VILLAGER_NO, 1f, .5f)
            return
        }

        val location = target.location.centralize()

        if (furnitureType.rotatable && player.isSneaking) {
            val standDirection = player.location.toVector().subtract(location.toVector())
            location.setDirection(standDirection)
        }

        val placed = FurnitureManager.PlacedFurniture(
            UUID.randomUUID().toString().split("-")[0],
            furnitureType.name,
            target.location,
            item.clone(),
            playerBlockFace,
            location.yaw
        )

        manager.createPlacedFurniture(placed)

        blocks.forEach {
            it.type = Material.BARRIER
            it.setMetadata("furniture", FixedMetadataValue(BitwigsPlugin.instance, placed.id))
        }

        item.amount--
    }

}