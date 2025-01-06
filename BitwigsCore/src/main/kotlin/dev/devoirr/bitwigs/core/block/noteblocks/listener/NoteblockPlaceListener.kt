package dev.devoirr.bitwigs.core.block.noteblocks.listener

import dev.devoirr.bitwigs.core.block.noteblocks.NoteblocksManager
import dev.devoirr.bitwigs.core.subType
import org.bukkit.GameEvent
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.NotePlayEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.GenericGameEvent
import org.bukkit.scheduler.BukkitRunnable

class NoteblockPlaceListener(private val manager: NoteblocksManager) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockPhysics(event: BlockPhysicsEvent) {

        val block = event.block
        val aboveBlock = block.getRelative(BlockFace.UP)
        val belowBlock = block.getRelative(BlockFace.DOWN)

        if (belowBlock.type == Material.NOTE_BLOCK) {
            event.isCancelled = true
            updateAndCheck(belowBlock)
        } else if (aboveBlock.type == Material.NOTE_BLOCK) {
            event.isCancelled = true
            updateAndCheck(aboveBlock)
        }

        if (block.type == Material.NOTE_BLOCK) {
            event.isCancelled = true
            updateAndCheck(aboveBlock)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onNoteblockPowered(event: GenericGameEvent) {

        val block = event.location.block
        val eLoc = block.location

        if (event.event != GameEvent.NOTE_BLOCK_PLAY)
            return
        if (block.type != Material.NOTE_BLOCK)
            return

        val data = block.blockData.clone() as NoteBlock
        manager.getTaskManager().runTaskLater(object : BukkitRunnable() {
            override fun run() {
                block.setBlockData(data, false)
            }
        }, 1L)
    }

    @EventHandler
    fun onNote(event: NotePlayEvent) {
        val block = event.block
        val type = manager.getTypeByBlock(block) ?: return

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    fun onPlaceAgainstNoteBlock(event: PlayerInteractEvent) {

        if (!event.hasItem())
            return

        val block = event.clickedBlock ?: return

        val item = event.item ?: return
        val type = manager.getTypeByItemStack(item) ?: return

        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.useInteractedBlock() == Event.Result.DENY) return

        event.setUseInteractedBlock(Event.Result.DENY)

        val target: Block = if (block.isReplaceable) {
            block
        } else {
            block.getRelative(event.blockFace)
        }

        target.type = Material.NOTE_BLOCK
        target.setBlockData(type.subType().createBlockData(), false)

        if (event.player.gameMode != GameMode.CREATIVE) {
            item.amount--
        }

        event.player.playSound(event.player, Sound.BLOCK_STONE_PLACE, 0.5f, 1f)

    }

    private fun updateAndCheck(block: Block) {
        val blockAbove = block.getRelative(BlockFace.UP)
        if (blockAbove.type == Material.NOTE_BLOCK)
            block.state.update(true, true)
        val nextBlock = blockAbove.getRelative(BlockFace.UP)
        if (nextBlock.type == Material.NOTE_BLOCK)
            updateAndCheck(blockAbove)
    }
}