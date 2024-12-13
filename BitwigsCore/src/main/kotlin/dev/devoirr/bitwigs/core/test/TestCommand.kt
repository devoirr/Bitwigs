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
                itemStack = { _ -> ItemStack(Material.GRAY_STAINED_GLASS_PANE) },
                action = {}
            ),
            45, 46, 47, 48, 49, 50, 51, 52, 53
        )

        Material.entries.forEach { material ->
            testMenu.addFillerItem(
                MenuButton(
                    itemStack = { _ -> ItemStack(material) },
                    action = { }
                )
            )
        }

        testMenu.open(p0)

        return true

    }
}