package dev.devoirr.bitwigs.core.blocks.action

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.ReplacedBlocks
import dev.devoirr.bitwigs.core.blocks.action.model.database.PlacedActionBlockRow
import dev.devoirr.bitwigs.core.locale.Locale
import dev.devoirr.bitwigs.core.toString
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

@CommandAlias("actionblocks|ab")
@CommandPermission("bitwigs.clickableblocks")
class ActionBlockCommand(private val manager: ActionBlocksManager) : BaseCommand() {

    @Subcommand("addblock")
    @Syntax("<Type>")
    @Description("Добавляет экшен-блок определённого вида")
    @CommandCompletion("@actionblocktypes")
    fun addBlock(player: Player, typeName: String) {

        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            Locale.mustLookAtBlock.send(player)
            return
        }

        if (targetBlock.hasMetadata("action_block")) {
            Locale.alreadyActionBlock.send(player)
            return
        }

        val type = manager.getType(typeName)
        if (type == null) {
            Locale.actionBlockTypeNotFound.send(player)
            return
        }

        val clickableBlockRow = PlacedActionBlockRow()
        clickableBlockRow.type = typeName
        clickableBlockRow.location = targetBlock.location.toString(block = true)

        manager.database.write(clickableBlockRow)

        targetBlock.setMetadata("action_block", FixedMetadataValue(BitwigsPlugin.instance, typeName))

        Locale.actionBlockCreated.send(player)

    }

    @Subcommand("removeblock")
    @Description("Удаляет экшен-блок")
    fun removeBlock(player: Player, args: Array<String>) {
        val targetBlock = player.getTargetBlockExact(5)
        if (targetBlock == null) {
            Locale.mustLookAtBlock.send(player)
            return
        }

        if (!targetBlock.hasMetadata("action_block")) {
            Locale.notActionBlock.send(player)
            return
        }

        val type = targetBlock.getMetadata("action_block")[0].asString()

        targetBlock.removeMetadata("action_block", BitwigsPlugin.instance)
        targetBlock.removeMetadata("cooldown", BitwigsPlugin.instance)

        ReplacedBlocks.reset(targetBlock)
        ReplacedBlocks.removeBlock(targetBlock)

        val row = PlacedActionBlockRow()
        row.type = type
        row.location = targetBlock.location.toString(block = true)

        manager.database.delete(row)
        Locale.actionBlockRemoved.send(player)

    }

}