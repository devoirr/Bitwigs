package dev.devoirr.bitwigs.core.frames.listener

import dev.devoirr.bitwigs.core.frames.FramesManager
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class FrameListener(private val manager: FramesManager) : Listener {

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {

        if (!event.hasItem() || !event.hasBlock())
            return

        val block = event.clickedBlock ?: return
        val itemStack = event.item ?: return

        if (itemStack.type != Material.ITEM_FRAME)
            return

        event.isCancelled = true

        val blockFace = event.blockFace

        manager.create(block, blockFace)
        itemStack.amount--

    }

}

fun org.bukkit.block.BlockFace.toFrameData(): Int {
    return when (this) {
        org.bukkit.block.BlockFace.UP -> 0
        org.bukkit.block.BlockFace.DOWN -> 1
        org.bukkit.block.BlockFace.NORTH -> 3
        org.bukkit.block.BlockFace.SOUTH -> 2
        org.bukkit.block.BlockFace.WEST -> 5
        org.bukkit.block.BlockFace.EAST -> 4
        else -> 0
    }
}