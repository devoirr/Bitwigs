package dev.devoirr.bitwigs.core.block.noteblocks.event

import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockSubType
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class NoteblockInteractEvent(
    val noteblockType: NoteblockSubType,
    val player: Player,
    val block: Block,
    val itemInHand: ItemStack,
    val blockFace: BlockFace,
    val hand: EquipmentSlot,
    val action: org.bukkit.event.block.Action
) : Event(), Cancellable {

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }

    }

    private var canceled = false

    override fun isCancelled(): Boolean {
        return canceled
    }

    override fun setCancelled(p0: Boolean) {
        this.canceled = p0
    }

    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }
}