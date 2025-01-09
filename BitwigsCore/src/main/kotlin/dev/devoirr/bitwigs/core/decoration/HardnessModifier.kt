package dev.devoirr.bitwigs.core.decoration

import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

interface HardnessModifier {

    fun isCalledForBlock(block: Block): Boolean
    fun breakBlock(block: Block)
    fun getPeriodForBlock(block: Block, itemStack: ItemStack): Long

}