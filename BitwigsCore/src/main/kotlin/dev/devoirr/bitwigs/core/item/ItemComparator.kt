package dev.devoirr.bitwigs.core.item

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class ItemComparator(
    val material: Material,
    val modelData: List<Int>,
    val displayName: Component? = null,
    val lore: List<Component>? = null
) {
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