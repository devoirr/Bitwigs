package dev.devoirr.bitwigs.core.item

import dev.devoirr.bitwigs.core.toComponent
import dev.devoirr.bitwigs.core.toIntegerList
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

data class ItemComparator(
    val material: Material,
    val modelData: List<Int>,
    val displayName: Component? = null,
    val lore: List<Component>? = null
) {

    companion object {
        fun parse(section: ConfigurationSection): ItemComparator {
            val materialString = section.getString("material") ?: "stone"
            val material = Material.matchMaterial(materialString.uppercase()) ?: Material.STONE
            val modelDataString = section.getString("model-data") ?: "0"
            val modelDataList = modelDataString.toIntegerList()
            val displayName = section.getString("name")?.toComponent()
            val lore: List<Component>? =
                if (section.getKeys(false).contains("lore")) section.getStringList("lore").map { it.toComponent() }
                else null
            return ItemComparator(material, modelDataList, displayName, lore)
        }
    }

    fun isThisItem(itemStack: ItemStack): Boolean {
        if (itemStack.type != material)
            return false
        if (!itemStack.hasItemMeta())
            return false
        if (!itemStack.itemMeta.hasCustomModelData())
            return false
        if (itemStack.itemMeta.customModelData !in modelData)
            return false
        displayName?.let {
            if (!itemStack.itemMeta.hasDisplayName())
                return false
            if (itemStack.itemMeta.displayName() != displayName)
                return false
        }
        lore?.let {
            if (!itemStack.itemMeta.hasLore())
                return false
            if (itemStack.itemMeta.lore() != lore)
                return false
        }
        return true
    }
}