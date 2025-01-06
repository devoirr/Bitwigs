package dev.devoirr.bitwigs.core.gui.listener

import com.google.common.cache.CacheBuilder
import dev.devoirr.bitwigs.core.gui.Menu
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import java.time.Duration
import java.util.*

class MenuListener : Listener {

    private val allowedClickTypes =
        mutableListOf(ClickType.RIGHT, ClickType.LEFT, ClickType.SHIFT_RIGHT, ClickType.SHIFT_LEFT)

    private val cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMillis(20)).build<UUID, Byte>()

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory ?: return
        val session = Menu.menuSessionMap[event.whoClicked.uniqueId] ?: return

        val menu = session.menu
        val player = event.whoClicked as Player

        if (clickedInventory == player.openInventory.topInventory) {
            event.isCancelled = !menu.parameters.clickable

            if (!allowedClickTypes.contains(event.click))
                event.isCancelled = true

            if (!cache.asMap().containsKey(player.uniqueId)) {
                menu.handleClick(event)
                cache.put(player.uniqueId, 0x0)
            }
        } else {
            if (event.isShiftClick || event.click.isKeyboardClick) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.reason != InventoryCloseEvent.Reason.OPEN_NEW && event.reason != InventoryCloseEvent.Reason.PLUGIN)
            Menu.menuSessionMap.remove(event.player.uniqueId)
    }

}