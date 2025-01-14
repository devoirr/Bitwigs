package dev.devoirr.bitwigs.core.blocks.clickable

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.clickable.model.database.PlacedClickableBlockRow
import dev.devoirr.bitwigs.core.toString
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

@CommandAlias("droppingblocks|db")
@CommandPermission("bitwigs.clickableblocks")
class ClickableBlockCommand(private val manager: ClickableBlocksManager) : BaseCommand() {

    @Subcommand("addblock")
    @Syntax("<Type>")
    @Description("Добавляет экшен-блок определённого вида")
    @CommandCompletion("@clickableblocktypes")
    fun addBlock(player: Player, typeName: String) {

        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            player.sendMessage("You must look at some block.")
            return
        }

        if (targetBlock.hasMetadata("clickable_block")) {
            player.sendMessage("This block is already clickable-block!")
            return
        }

        val type = manager.getType(typeName)
        if (type == null) {
            player.sendMessage("Type not found.")
            return
        }

        val clickableBlockRow = PlacedClickableBlockRow()
        clickableBlockRow.type = typeName
        clickableBlockRow.location = targetBlock.location.toString(block = true)

        manager.database.write(clickableBlockRow)

        targetBlock.setMetadata("clickable_block", FixedMetadataValue(BitwigsPlugin.instance, typeName))

        player.sendMessage("Successfully created.")

    }

}