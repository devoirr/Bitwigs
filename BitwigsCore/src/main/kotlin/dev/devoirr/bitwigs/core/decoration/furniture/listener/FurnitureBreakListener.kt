package dev.devoirr.bitwigs.core.decoration.furniture.listener

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.listener.Listener
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class FurnitureBreakListener(private val manager: FurnitureManager) : Listener() {

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {

        val block = event.block
        if (!block.hasMetadata("furniture"))
            return

        val placed = manager.getPlacedFurniture(block.getMetadata("furniture")[0].asString()) ?: return

        val center = placed.center.centralize()
        val itemStack = placed.item

        val type = manager.getFurnitureType(placed.type) ?: return
        val blocks = type.hitbox.getBlocks(center.block, placed.blockFace)

        blocks.forEach {
            it.type = Material.AIR;
            it.removeMetadata("funriture", BitwigsPlugin.instance)
        }

        manager.deletePlacedFurniture(placed.id)
        center.world.dropItemNaturally(center, itemStack)

    }

}