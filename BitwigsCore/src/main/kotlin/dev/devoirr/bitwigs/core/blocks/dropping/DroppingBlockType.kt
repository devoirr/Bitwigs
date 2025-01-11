package dev.devoirr.bitwigs.core.blocks.dropping

import dev.devoirr.bitwigs.core.blocks.BlockEffect

data class DroppingBlockType(
    val lootTime: Int,
    val refillTime: Int,
    val refillAfterLoots: Int,
    val lootEffect: BlockEffect?,
    val refillEffect: BlockEffect?,
    val items: List<DroppingBlockItem>
)
