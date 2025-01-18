package dev.devoirr.bitwigs.core.gui

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.listener.Listener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.*
import org.bukkit.scheduler.BukkitRunnable

class MenuListener : Listener() {

    companion object {
        val allowedActions =
            mutableSetOf(
                InventoryAction.PICKUP_ALL,
//                InventoryAction.PICKUP_ONE,
//                InventoryAction.PICKUP_HALF,
//                InventoryAction.PICKUP_SOME,
                InventoryAction.PLACE_ALL,
//                InventoryAction.PLACE_ONE,
//                InventoryAction.PLACE_SOME,
//                InventoryAction.MOVE_TO_OTHER_INVENTORY
            )
    }

    @EventHandler(ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val holder = MenuManager.getPlayerMenuHolder(player) ?: return

        if (player.isSleeping) {
            player.closeInventory()
            event.isCancelled = true
            return
        }

        if (event.action == InventoryAction.HOTBAR_SWAP) {
            event.isCancelled = true

            val itemInOffHand = player.inventory.itemInOffHand
            player.inventory.setItemInOffHand(null)

            try {
                object : BukkitRunnable() {
                    override fun run() {
                        player.inventory.setItemInOffHand(itemInOffHand)
                        player.updateInventory()
                    }
                }.runTask(BitwigsPlugin.instance)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (event.click == ClickType.DOUBLE_CLICK && event.hotbarButton != -1) {
            event.isCancelled = true
            return
        }

        if (event.isShiftClick && holder.inventory != event.clickedInventory) {
            event.isCancelled = true
        }

        val clickedInventory = event.clickedInventory ?: return
        if (clickedInventory != player.openInventory.topInventory) {
            return
        }
        
        if (event.action !in allowedActions) {
            event.isCancelled = true
            return
        }

        event.isCancelled = true

        val menu = MenuManager.getMenu(holder.menuId)
        menu?.runHandlers(event)

        val action = holder.actions[event.slot] ?: return
        action.invoke(event, player)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        (event.player as? Player)?.let { MenuManager.closeMenuHolderForPlayer(it) }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {

        val player = (event.whoClicked as? Player) ?: return
        val inventory = event.inventory

        if (inventory != player.openInventory.topInventory)
            return

        MenuManager.getPlayerMenuHolder(player) ?: return
        event.isCancelled = true
    }

}