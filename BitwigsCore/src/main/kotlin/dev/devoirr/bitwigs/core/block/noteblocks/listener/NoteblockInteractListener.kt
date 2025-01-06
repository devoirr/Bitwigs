package dev.devoirr.bitwigs.core.block.noteblocks.listener

import dev.devoirr.bitwigs.core.block.noteblocks.NoteblocksManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.NotePlayEvent

class NoteblockInteractListener(private val manager: NoteblocksManager) : Listener {

    @EventHandler
    fun onClick(event: NotePlayEvent) {
        event.isCancelled = true
    }

}