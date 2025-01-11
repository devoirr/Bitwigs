package dev.devoirr.bitwigs.core.blocks.dropping.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlocksManager
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockItem
import dev.devoirr.bitwigs.core.blocks.dropping.model.database.PlacedDroppingBlockRow
import dev.devoirr.bitwigs.core.toString
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

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

    @Subcommand("addblock")
    @Syntax("<Type>")
    @Description("Добавляет дроп-блок определённого вида")
    @CommandCompletion("@droppingblocktypes")
    fun addBlock(player: Player, typeName: String) {

        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            player.sendMessage("You must look at some block.")
            return
        }

        val type = manager.getType(typeName)
        if (type == null) {
            player.sendMessage("Type not found.")
            return
        }

        val placedDroppingBlockRow = PlacedDroppingBlockRow()
        placedDroppingBlockRow.type = typeName
        placedDroppingBlockRow.location = targetBlock.location.toString(block = true)

        manager.database.write(placedDroppingBlockRow)

        targetBlock.setMetadata("dropping_block", FixedMetadataValue(BitwigsPlugin.instance, typeName))

        player.sendMessage("Successfully created.")

    }

    @Subcommand("testblock")
    fun testBlock(player: Player) {

        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            player.sendMessage("You must look at some block.")
            return
        }

        if (targetBlock.hasMetadata("dropping_block")) {
            player.sendMessage(targetBlock.getMetadata("dropping_block")[0].asString())
            return
        }

        player.sendMessage("Not a dropping block.")

    }

}