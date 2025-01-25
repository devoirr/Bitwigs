package dev.devoirr.bitwigs.core.frames.listener

import dev.devoirr.bitwigs.core.frames.FramesManager
import dev.devoirr.bitwigs.core.frames.PacketFrame
import dev.devoirr.bitwigs.core.listener.Listener
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom

class FramesListener(private val manager: FramesManager) : Listener() {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {

        if (!event.hasItem() || !event.hasBlock())
            return

        val item = event.item ?: return
        val block = event.clickedBlock ?: return

        if (item.type != Material.ITEM_FRAME)
            return

        event.isCancelled = true
        event.setUseItemInHand(Event.Result.DENY)

        val frame = PacketFrame(
            ThreadLocalRandom.current().nextInt(1000),
            block.getRelative(event.blockFace).location,
            event.blockFace.oppositeFace,
            1,
            ItemStack(Material.STONE)
        )

        frame.create()
        item.amount--

    }

}