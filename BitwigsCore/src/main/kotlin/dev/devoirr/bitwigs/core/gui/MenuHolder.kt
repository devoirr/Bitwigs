package dev.devoirr.bitwigs.core.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import java.util.*

class MenuHolder(
    val menuId: UUID,
    val inventory: Inventory,
    val actions: Map<Int, (InventoryClickEvent, Player) -> Unit>
)