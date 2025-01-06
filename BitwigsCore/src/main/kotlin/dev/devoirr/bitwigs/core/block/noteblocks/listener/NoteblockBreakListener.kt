package dev.devoirr.bitwigs.core.block.noteblocks.listener

import dev.devoirr.bitwigs.core.block.noteblocks.NoteblocksManager
import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockSubType
import dev.devoirr.bitwigs.core.subType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class NoteblockBreakListener(private val manager: NoteblocksManager) : Listener {

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {

        if (event.block.type != Material.NOTE_BLOCK)
            return

        val block = event.block
        val type = manager.getTypeByBlock(block) ?: return

        block.type = Material.AIR
        prepareAndDropItem(type.subType(), block.location.clone().add(0.5, 0.5, 0.5))

        updateAndCheck(block.getRelative(BlockFace.DOWN))
    }

    private fun updateAndCheck(block: Block) {
        val blockAbove = block.getRelative(BlockFace.UP)
        if (blockAbove.type == Material.NOTE_BLOCK)
            block.state.update(true, true)
        val nextBlock = blockAbove.getRelative(BlockFace.UP)
        if (nextBlock.type == Material.NOTE_BLOCK)
            updateAndCheck(blockAbove)
    }

    private fun prepareAndDropItem(type: NoteblockSubType, location: Location) {

        val itemStack = ItemStack(Material.SUGAR)
        val itemMeta = itemStack.itemMeta

        itemMeta.displayName(type.name)
        itemMeta.setCustomModelData(type.modelData)

        itemStack.itemMeta = itemMeta

        location.world.dropItemNaturally(location, itemStack)

    }

}