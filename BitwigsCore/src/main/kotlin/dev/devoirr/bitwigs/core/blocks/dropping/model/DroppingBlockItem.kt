package dev.devoirr.bitwigs.core.blocks.dropping.model

import dev.devoirr.bitwigs.core.randomizer.ItemWithChance
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class DroppingBlockItem(val itemStack: ItemStack, val defaultChance: Double) : ItemWithChance {

    companion object {
        fun parse(section: ConfigurationSection): DroppingBlockItem {
            val itemStack = section.getItemStack("item", ItemStack(Material.STICK))!!
            val chance = section.getDouble("chance", 0.0)

            return DroppingBlockItem(itemStack, chance)
        }

        fun write(item: DroppingBlockItem, section: ConfigurationSection) {
            section.set("item", item.itemStack)
            section.set("chance", item.defaultChance)
        }
    }

    override fun getChance(): Double {
        return defaultChance
    }

    fun copyWithChange(chance: Double): DroppingBlockItem {
        return DroppingBlockItem(itemStack, chance)
    }
}