package dev.devoirr.bitwigs.core.decoration

import dev.devoirr.bitwigs.core.blocks.BlockEffect
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

interface DecorationMechanic {

    fun isThisMechanic(block: Block): Boolean
    fun getHardnessModifier(block: Block): HardnessModifier
    fun getEffect(block: Block, interactionType: InteractionType): BlockEffect?

    interface HardnessModifier {

        fun isCalledForBlock(block: Block): Boolean
        fun breakBlock(block: Block)
        fun getPeriodForBlock(block: Block, itemStack: ItemStack): Long

    }

}