package dev.devoirr.bitwigs.core.gui

import dev.devoirr.bitwigs.core.BitwigsPlugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class Menu {

    private val menuID = UUID.randomUUID()

    private var title: Component = Component.text("Menu").color(NamedTextColor.BLACK)
    private var size: Int = 9

    private val items = mutableMapOf<Int, (Player) -> ItemStack>()
    private val actions = mutableMapOf<Int, (InventoryClickEvent, Player) -> Unit>()

    private val handlers = mutableListOf<(InventoryClickEvent) -> Unit>()

    fun title(component: Component): Menu {
        this.title = component
        return this
    }

    fun size(size: Int): Menu {
        this.size = size
        return this
    }

    fun handler(handler: (InventoryClickEvent) -> Unit): Menu {
        handlers.add(handler)
        return this
    }

    fun item(itemStack: (Player) -> ItemStack, action: (InventoryClickEvent, Player) -> Unit, vararg slots: Int): Menu {
        for (slot in slots) {
            items[slot] = itemStack
            actions[slot] = action
        }
        return this
    }

    fun item(itemStack: (Player) -> ItemStack, vararg slots: Int): Menu {
        for (slot in slots) {
            items[slot] = itemStack
        }
        return this
    }

    fun addItem(itemStack: (Player) -> ItemStack, action: (InventoryClickEvent, Player) -> Unit): Menu {
        val slots = (0..<size).filter { it !in items.keys }
        if (slots.isEmpty())
            return this

        val slot = slots[0]
        return item(itemStack, action, slot)
    }

    fun addItem(
        itemStack: (Player) -> ItemStack,
        action: (InventoryClickEvent, Player) -> Unit,
        minimalSlot: Int
    ): Menu {
        val slots = (0..<size).filter { it !in items.keys }
        if (slots.isEmpty())
            return this

        val slot = slots.firstOrNull { it >= minimalSlot }
        if (slot == null)
            return this
        
        return item(itemStack, action, slot)
    }

    fun runHandlers(event: InventoryClickEvent) {
        handlers.forEach { it(event) }
    }

    fun openFor(player: Player) {

        val inventory = Bukkit.createInventory(player, size, title)

        for (slot in items.keys) {

            if (slot >= size) {
                BitwigsPlugin.instance.logger.info("Failed to place Menu item on slot $slot, since the menu-size is only $size")
                continue
            }

            inventory.setItem(slot, items[slot]!!(player))

        }

        val holder = MenuHolder(menuID, inventory, actions)
        player.openInventory(inventory)

        MenuManager.addMenuHolderForPlayer(player, holder)

    }

    fun register(): Menu {
        MenuManager.registerMenu(this, menuID)
        return this
    }

}