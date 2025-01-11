package dev.devoirr.bitwigs.core.blocks.dropping.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlockItem
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlocksManager
import org.bukkit.Material
import org.bukkit.entity.Player

@CommandAlias("droppingblocks|db")
@CommandPermission("bitwigs.droppingblocks")
class DroppingBlocksCommand(private val manager: DroppingBlocksManager) : BaseCommand() {

    @Subcommand("additem")
    @Syntax("<ID> <Default Chance>")
    @Description("Добавляет предмет, выпадающий из дроп-блоков")
    fun addItem(player: Player, id: String, chance: Double) {
        var section = manager.config.get().getConfigurationSection("items")
        if (section == null) {
            section = manager.config.get().createSection("items")
        }

        if (section.getKeys(false).contains(id)) {
            player.sendMessage("ID already used.")
            return
        }

        val itemStack = player.inventory.itemInMainHand
        if (itemStack.type == Material.AIR) {
            player.sendMessage("You need to hold an item.")
            return
        }

        val item = DroppingBlockItem(itemStack, chance)
        val itemSection = section.createSection(id)

        BitwigsFactory.droppingBlockItemFactory.write(item, itemSection)

        manager.config.save()
        player.sendMessage("Saved successfully.")
    }

}