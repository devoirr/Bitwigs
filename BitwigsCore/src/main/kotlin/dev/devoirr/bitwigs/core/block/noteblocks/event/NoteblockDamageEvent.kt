package dev.devoirr.bitwigs.core.block.noteblocks.event

import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockSubType
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class NoteblockDamageEvent(
    val noteblockType: NoteblockSubType,
    val player: Player,
    val block: Block
) : Event(), Cancellable {

    private var canceled = false

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }

    }

    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }

    override fun isCancelled(): Boolean {
        return canceled
    }

    override fun setCancelled(p0: Boolean) {
        this.canceled = p0
    }
}