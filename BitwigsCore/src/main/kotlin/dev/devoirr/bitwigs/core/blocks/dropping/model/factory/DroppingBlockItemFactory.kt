package dev.devoirr.bitwigs.core.blocks.dropping.model.factory

import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockItem
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class DroppingBlockItemFactory : Factory<DroppingBlockItem> {

    override fun parse(section: ConfigurationSection): DroppingBlockItem {
        val itemStack = section.getItemStack("item", ItemStack(Material.STICK))!!
        val chance = section.getDouble("chance", 0.0)

        return DroppingBlockItem(itemStack, chance)
    }

    override fun write(item: DroppingBlockItem, section: ConfigurationSection) {
        section.set("item", item.itemStack)
        section.set("chance", item.defaultChance)
    }
}