package dev.devoirr.bitwigs.core.decoration.noteblocks.listener

import dev.devoirr.bitwigs.core.decoration.noteblocks.NoteblocksManager
import dev.devoirr.bitwigs.core.listener.Listener
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

class NoteblockPlaceListener(private val manager: NoteblocksManager) : Listener() {

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {

        if (!event.action.isRightClick)
            return
        if (!event.hasItem() || !event.hasBlock())
            return

        val item = event.item ?: return
        val block = event.clickedBlock ?: return
        val blockFace = event.blockFace

        val typePair = manager.getTypeAndSubtype(item) ?: return
        val subType = typePair.second

        val target = if (block.isReplaceable) block else block.getRelative(blockFace)

        target.type = Material.NOTE_BLOCK
        target.blockData = subType.createBlockData()

        item.amount--
        event.player.setCooldown(Material.SUGAR, 2)

    }

}