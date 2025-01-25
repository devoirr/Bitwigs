package dev.devoirr.bitwigs.core.decoration.noteblocks.listener

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.decoration.noteblocks.NoteblocksManager
import dev.devoirr.bitwigs.core.listener.Listener
import org.bukkit.ExplosionResult
import org.bukkit.GameEvent
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.world.GenericGameEvent
import org.bukkit.scheduler.BukkitRunnable

class NoteblockListener(private val manager: NoteblocksManager) : Listener() {

    @EventHandler
    fun onPowered(event: GenericGameEvent) {

        val block = event.location.block
        val eLoc = block.location
        if (event.event != GameEvent.NOTE_BLOCK_PLAY)
            return

        val data = (block.blockData as NoteBlock).clone()
        object : BukkitRunnable() {
            override fun run() {
                block.setBlockData(data, false)
            }
        }.runTaskLater(BitwigsPlugin.instance, 1L)

    }

    @EventHandler
    fun onPistonPush(event: BlockPistonExtendEvent) {
        if (event.blocks.any { it.type == Material.NOTE_BLOCK }) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPistonPull(event: BlockPistonRetractEvent) {
        if (event.blocks.any { it.type == Material.NOTE_BLOCK }) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPhysics(event: BlockPhysicsEvent) {

        val block = event.block
        val aboveBlock = block.getRelative(BlockFace.UP)
        val belowBlock = block.getRelative(BlockFace.DOWN)

        // If block below is NoteBlock, it will be affected by the break
        // Call updateAndCheck from it to fix vertical stack of NoteBlocks
        // if belowBlock is not a NoteBlock we must ensure the above is not, if it is call updateAndCheck from block
        if (belowBlock.type == Material.NOTE_BLOCK) {
            event.isCancelled = true
            updateAndCheck(belowBlock)
        } else if (aboveBlock.type == Material.NOTE_BLOCK) {
            event.isCancelled = true
            updateAndCheck(aboveBlock)
        }
        if (block.type == Material.NOTE_BLOCK) {
            event.isCancelled = true
            updateAndCheck(block)
        }
    }

    @EventHandler
    fun onPlayingNote(event: NotePlayEvent) {
        val block = event.block
        if (manager.getTypeAndSubtype(block) != null) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        val result = event.explosionResult
        if (result != ExplosionResult.DESTROY && result != ExplosionResult.DESTROY_WITH_DECAY) return

        for (block in HashSet(event.blockList())) {
            if (block.type != Material.NOTE_BLOCK)
                continue

            block.type = Material.AIR
            event.blockList().remove(block)
        }
    }

    @EventHandler
    fun onEntityExplode(event: EntityExplodeEvent) {
        val result = event.explosionResult
        if (result != ExplosionResult.DESTROY && result != ExplosionResult.DESTROY_WITH_DECAY) return

        for (block in HashSet(event.blockList())) {
            if (block.type != Material.NOTE_BLOCK)
                continue

            block.type = Material.AIR
            event.blockList().remove(block)
        }
    }


    private fun updateAndCheck(block: Block) {
        val blockAbove = block.getRelative(BlockFace.UP)
        if (blockAbove.type == Material.NOTE_BLOCK) blockAbove.state.update(true, true)
        val nextBlock = blockAbove.getRelative(BlockFace.UP)
        if (nextBlock.type == Material.NOTE_BLOCK) updateAndCheck(blockAbove)
    }

}