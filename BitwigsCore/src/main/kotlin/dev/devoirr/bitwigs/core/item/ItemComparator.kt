package dev.devoirr.bitwigs.core.item

import dev.devoirr.bitwigs.core.toIntegerList
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

data class ItemComparator(
    val material: Material,
    val modelData: List<Int>
) {

    companion object {
        fun parse(section: ConfigurationSection): ItemComparator {
            val materialString = section.getString("material") ?: "stone"
            val material = Material.matchMaterial(materialString.uppercase()) ?: Material.STONE
            val modelDataString = section.getString("model-data") ?: "0"
            val modelDataList = modelDataString.toIntegerList()
            return ItemComparator(material, modelDataList)
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
        return true
    }
}