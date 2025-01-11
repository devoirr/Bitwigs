package dev.devoirr.bitwigs.core.blocks.dropping

import dev.devoirr.bitwigs.core.randomizer.ItemWithChance
import org.bukkit.inventory.ItemStack

class DroppingBlockItem(val itemStack: ItemStack, val defaultChance: Double) : ItemWithChance {
    override fun getChance(): Double {
        return defaultChance
    }

    fun copyWithChange(chance: Double): DroppingBlockItem {
        return DroppingBlockItem(itemStack, chance)
    }
}