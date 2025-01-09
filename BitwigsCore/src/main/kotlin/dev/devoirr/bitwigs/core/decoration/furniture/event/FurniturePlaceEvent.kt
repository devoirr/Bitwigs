package dev.devoirr.bitwigs.core.decoration.furniture.event

import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureType
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

data class FurniturePlaceEvent(val player: Player, val block: Block, val type: FurnitureType) : Event(), Cancellable {

    private var isCancelled = false

    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() = handlerList
    }

    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }
}