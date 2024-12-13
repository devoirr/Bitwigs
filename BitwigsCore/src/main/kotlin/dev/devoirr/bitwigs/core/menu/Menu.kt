package dev.devoirr.bitwigs.core.menu

import com.google.gson.Gson
import dev.devoirr.bitwigs.core.menu.container.MenuButton
import dev.devoirr.bitwigs.core.menu.container.MenuPage
import dev.devoirr.bitwigs.core.menu.container.MenuParameters
import dev.devoirr.bitwigs.core.menu.session.MenuSession
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class Menu {

    companion object {
        val menuSessionMap = mutableMapOf<UUID, MenuSession>()
    }

    private val permanentButtons = mutableMapOf<Int, MenuButton>()
    var parameters: MenuParameters = MenuParameters(Component.text("Меню"), 54)

    private val pages = mutableListOf<MenuPage>()

    var currentPage = 0

    fun addPermanentButton(button: MenuButton, vararg slots: Int) {
        slots.forEach {
            permanentButtons[it] = button
        }
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
                inventory.setItem(slot, currentPage.list[index].itemStack(player))
            }

        }

        /* Permanent buttons */
        permanentButtons.forEach { (slot, button) -> inventory.setItem(slot, button.itemStack(player)) }

        player.openInventory(inventory)

    }

    fun clone(): Menu {
        val stringMenu = Gson().toJson(this, Menu::class.java)
        return Gson().fromJson(stringMenu, Menu::class.java)
    }

}