package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.furniture.model.fabric.BlockEffectFactory
import dev.devoirr.bitwigs.core.furniture.model.fabric.FurnitureItemComparatorFactory
import dev.devoirr.bitwigs.core.furniture.model.fabric.HitboxFactory
import dev.devoirr.bitwigs.core.furniture.model.fabric.PerItemParameterFactory

class BitwigsFactory {

    companion object {
        val blockEffectFactory = BlockEffectFactory()
        val furnitureItemComparatorFactory = FurnitureItemComparatorFactory()
        val perItemParameterFactory = PerItemParameterFactory()
        val hitboxFactory = HitboxFactory()
    }

}