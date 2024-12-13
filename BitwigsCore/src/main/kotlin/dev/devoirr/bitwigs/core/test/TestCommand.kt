package dev.devoirr.bitwigs.core.test

import dev.devoirr.bitwigs.core.menu.Menu
import dev.devoirr.bitwigs.core.menu.container.MenuButton
import dev.devoirr.bitwigs.core.menu.container.MenuParameters
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestCommand : BukkitCommand("test") {

    override fun execute(p0: CommandSender, p1: String, p2: Array<out String>?): Boolean {

        if (p0 !is Player)
            return true

        val testMenu = Menu()
        testMenu.parameters =
            MenuParameters(Component.text("Тестовое Меню").color(NamedTextColor.BLACK), 54, (0..44).toList())

        testMenu.addPermanentButton(
            MenuButton(
                itemStack = { _ -> ItemStack(Material.RED_STAINED_GLASS_PANE) },
                action = { event ->
                    testMenu.previousPage()
                    testMenu.open(event.whoClicked as Player)
                }
            ),
            45, 46, 47, 48
        )

        testMenu.addPermanentButton(
            MenuButton(
                itemStack = { _ -> ItemStack(Material.GREEN_STAINED_GLASS_PANE) },
                action = { event ->
                    testMenu.nextPage()
                    testMenu.open(event.whoClicked as Player)
                }
            ),
            50, 51, 52, 53
        )

        Material.entries.filter { it.isItem }.forEach { material ->
            testMenu.addFillerItem(
                MenuButton(
                    itemStack = { _ -> ItemStack(material) },
                    action = { e -> e.whoClicked.inventory.addItem(ItemStack(material)) }
                )
            )
        }

        testMenu.open(p0)

        return true

    }
}