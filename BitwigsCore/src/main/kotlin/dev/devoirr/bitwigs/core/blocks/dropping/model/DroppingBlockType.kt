package dev.devoirr.bitwigs.core.blocks.dropping.model

import dev.devoirr.bitwigs.core.blocks.BlockEffect

data class DroppingBlockType(
    val lootTime: Int,
    val refillTime: Int,
    val refillAfterLoots: Int,
    val lootEffect: BlockEffect?,
    val refillEffect: BlockEffect?,
    val canBeBroken: Boolean,
    val items: List<DroppingBlockItem>
)
