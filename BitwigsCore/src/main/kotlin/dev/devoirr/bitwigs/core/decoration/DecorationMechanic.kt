package dev.devoirr.bitwigs.core.decoration

import dev.devoirr.bitwigs.core.blocks.BlockEffect
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface DecorationMechanic {

    fun isThisMechanic(block: Block): Boolean
    fun getEffect(block: Block, interactionType: InteractionType): BlockEffect?

    fun breakBlock(block: Block, player: Player, itemStack: ItemStack)
    fun getPeriodForBlock(block: Block, itemStack: ItemStack): Long

}