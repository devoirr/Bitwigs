package dev.devoirr.bitwigs.core.blocks

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData

object ReplacedBlocks {

    private val blocks = mutableListOf<ReplacedBlockData>()

    fun add(blockData: ReplacedBlockData) {
        blocks.add(blockData)
    }

    fun removeBlock(block: Block) {
        blocks.removeAll { it.block == block }
    }

    fun onDisable() {
        for (block in blocks) {
            block.block.type = block.defaultMaterial
            block.block.blockData = block.defaultData
        }

        blocks.clear()
    }

    fun reset(block: Block) {

        val replacedBlockData = blocks.firstOrNull { it.block.location == block.location } ?: return

        block.type = replacedBlockData.defaultMaterial
        block.blockData = replacedBlockData.defaultData

    }

}

data class ReplacedBlockData(val block: Block, val defaultMaterial: Material, val defaultData: BlockData)