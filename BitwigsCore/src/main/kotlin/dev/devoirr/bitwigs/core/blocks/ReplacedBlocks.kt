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

}

data class ReplacedBlockData(val block: Block, val defaultMaterial: Material, val defaultData: BlockData)