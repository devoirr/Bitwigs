package dev.devoirr.bitwigs.core.blocks.dropping

interface DroppingBlocksSerivce {
    fun getItem(id: String): DroppingBlockItem?
}