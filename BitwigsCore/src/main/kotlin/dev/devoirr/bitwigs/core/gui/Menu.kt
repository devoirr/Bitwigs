package dev.devoirr.bitwigs.core.gui

import com.google.gson.Gson
import dev.devoirr.bitwigs.core.gui.container.MenuButton
import dev.devoirr.bitwigs.core.gui.container.MenuPage
import dev.devoirr.bitwigs.core.gui.container.MenuParameters
import dev.devoirr.bitwigs.core.gui.session.MenuSession
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

class Menu {

    companion object {
        val menuSessionMap = mutableMapOf<UUID, MenuSession>()
    }

    private val permanentButtons = mutableMapOf<Int, MenuButton>()
    var parameters: MenuParameters = MenuParameters(Component.text("Меню"), 54)

    private val pages = mutableListOf<MenuPage>()

    private var currentPage = 0

    fun addPermanentButton(button: MenuButton, vararg slots: Int) {
        slots.forEach {
            permanentButtons[it] = button
        }
    }

    fun nextPage() {
        currentPage = (currentPage + 1).coerceAtMost(pages.size - 1)
    }

    fun previousPage() {
        currentPage = (currentPage - 1).coerceAtLeast(0)
    }

    fun addFillerItem(button: MenuButton) {
        val page: MenuPage

        /* Create a new page */
        if (pages.isEmpty() || pages.last().isFull()) {

            page = MenuPage(list = mutableListOf(button), parameters.fillerSlots.size)
            pages.add(page)

            return
        }

        /* Modify existing page */
        page = pages.last()
        page.list.add(button)
    }

    fun open(player: Player) {

        val inventory = Bukkit.createInventory(null, parameters.size, parameters.title)

        if (pages.isNotEmpty() && pages.size > currentPage) {

            val currentPage = pages[currentPage]
            parameters.fillerSlots.forEachIndexed { index, slot ->
                if (currentPage.list.size > index)
                    inventory.setItem(slot, currentPage.list[index].itemStack(player))
            }

        }

        /* Permanent buttons */
        permanentButtons.forEach { (slot, button) -> inventory.setItem(slot, button.itemStack(player)) }

        val session = MenuSession(player, this, System.currentTimeMillis())
        menuSessionMap[player.uniqueId] = session

        player.openInventory(inventory)

    }

    fun handleClick(event: InventoryClickEvent) {

        val slot = event.slot

        if (slot in permanentButtons.keys) {
            permanentButtons[slot]?.action?.let { it(event) }
            return
        }

        if (slot in parameters.fillerSlots) {
            val button = pages[currentPage].list[parameters.fillerSlots.indexOf(slot)]
            button.action(event)
        }

    }

    fun clone(): Menu {
        val stringMenu = Gson().toJson(this, Menu::class.java)
        return Gson().fromJson(stringMenu, Menu::class.java)
    }

}