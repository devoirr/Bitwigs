package dev.devoirr.bitwigs.core.menu.container

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

data class MenuButton(val itemStack: (Player) -> ItemStack, val action: (InventoryClickEvent) -> Unit)
