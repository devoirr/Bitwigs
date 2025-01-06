package dev.devoirr.bitwigs.core.block.noteblocks.model.type

import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

data class NoteblockType(
    private val key: String,
    private val breakingTime: dev.devoirr.bitwigs.core.block.furniture.model.small.PerItemParameter,
    private val dropWhenBreakingWith: List<String>,
    private val blocks: List<NoteblockSubType>
) {

    fun getSubTypeOf(block: Block): NoteblockSubType? {
        return blocks.firstOrNull { it.isThisType(block) }
    }

    fun getSubTypeOf(itemStack: ItemStack): NoteblockSubType? {
        return blocks.firstOrNull { it.isThisType(itemStack) }
    }

}