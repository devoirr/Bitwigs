package dev.devoirr.bitwigs.core.blocks.dropping.model.event

import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockType
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class DroppingBlockBreakEvent(
    val player: Player?,
    val type: DroppingBlockType,
    val block: Block
) : Event(), Cancellable {

    companion object {
        private val HANDLER_LIST = HandlerList()

        @JvmStatic
        fun getHandlerList() = HANDLER_LIST
    }

    private var isCancelled = false

    override fun getHandlers(): HandlerList {
        return HANDLER_LIST
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }

}