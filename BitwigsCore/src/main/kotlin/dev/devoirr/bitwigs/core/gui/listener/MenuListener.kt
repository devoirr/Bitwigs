package dev.devoirr.bitwigs.core.gui.listener

import dev.devoirr.bitwigs.core.gui.Menu
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class MenuListener : Listener {

    private val allowedClickTypes =
        mutableListOf(ClickType.RIGHT, ClickType.LEFT, ClickType.SHIFT_RIGHT, ClickType.SHIFT_LEFT)

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory ?: return

        val session = Menu.menuSessionMap[event.whoClicked.uniqueId] ?: return
        val menu = session.menu

        val player = event.whoClicked as Player

        if (!menu.parameters.clickable && clickedInventory == player.openInventory.topInventory)
            event.isCancelled = true

        if (clickedInventory == player.openInventory.bottomInventory && event.isShiftClick)
            event.isCancelled = true

        if (!allowedClickTypes.contains(event.click))
            event.isCancelled = true

        if (clickedInventory == player.openInventory.topInventory)
            menu.handleClick(event)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.reason != InventoryCloseEvent.Reason.OPEN_NEW && event.reason != InventoryCloseEvent.Reason.PLUGIN)
            Menu.menuSessionMap.remove(event.player.uniqueId)
    }

}