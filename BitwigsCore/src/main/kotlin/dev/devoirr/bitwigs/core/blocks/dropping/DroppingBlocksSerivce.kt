package dev.devoirr.bitwigs.core.blocks.dropping

import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockItem

interface DroppingBlocksSerivce {
    fun getItem(id: String): DroppingBlockItem?
}