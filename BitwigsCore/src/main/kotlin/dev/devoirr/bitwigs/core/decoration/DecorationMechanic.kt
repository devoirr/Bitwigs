package dev.devoirr.bitwigs.core.decoration

import dev.devoirr.bitwigs.core.decoration.model.BlockEffect
import org.bukkit.block.Block

interface DecorationMechanic {

    fun isThisMechanic(block: Block): Boolean
    fun getHardnessModifier(block: Block): HardnessModifier
    fun getEffect(block: Block, interactionType: InteractionType): BlockEffect?

}