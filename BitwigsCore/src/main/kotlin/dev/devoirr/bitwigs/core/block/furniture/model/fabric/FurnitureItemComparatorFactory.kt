package dev.devoirr.bitwigs.core.block.furniture.model.fabric

import dev.devoirr.bitwigs.core.block.furniture.model.small.FurnitureItemComparator
import dev.devoirr.bitwigs.core.toComponent
import dev.devoirr.bitwigs.core.toIntegerList
import dev.devoirr.bitwigs.core.util.factory.Factory
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class FurnitureItemComparatorFactory : Factory<FurnitureItemComparator> {

    override fun parse(section: ConfigurationSection): FurnitureItemComparator {
        val materialString = section.getString("material") ?: "stone"
        val material = Material.matchMaterial(materialString.uppercase()) ?: Material.STONE

        val modelDataString = section.getString("model-data") ?: "0"
        val modelDataList = modelDataString.toIntegerList()

        val displayName = section.getString("name")?.toComponent()
        val lore: List<Component>? =
            if (section.getKeys(false).contains("lore")) section.getStringList("lore").map { it.toComponent() }
            else null

        return FurnitureItemComparator(material, modelDataList, displayName, lore)
    }

    override fun write(section: ConfigurationSection) {

    }
}