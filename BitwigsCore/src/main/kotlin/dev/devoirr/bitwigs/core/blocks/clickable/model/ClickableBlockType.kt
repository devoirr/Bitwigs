package dev.devoirr.bitwigs.core.blocks.clickable.model

import dev.devoirr.bitwigs.core.blocks.BlockEffect

class ClickableBlockType(
    val permission: String,
    val commands: List<String>,
    val effect: BlockEffect?
)