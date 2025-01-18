package dev.devoirr.bitwigs.core.decoration.model

import org.bukkit.inventory.ItemStack

enum class Tool {

    AXE,
    PICKAXE,
    SHOVEL,
    HOE;

    companion object {
        private val hierarchy = mutableListOf("WOOD", "STONE", "IRON", "GOLD", "DIAMOND", "NETHERITE")

        fun getPriority(itemStack: ItemStack): Int {
            return hierarchy.indexOf(itemStack.type.name.split("_")[0])
        }
    }

    fun isThisTool(itemStack: ItemStack): Boolean {
        return when (this) {
            AXE -> itemStack.type.name.endsWith("_AXE")
            PICKAXE -> itemStack.type.name.endsWith("_PICKAXE")
            SHOVEL -> itemStack.type.name.endsWith("_SHOVEL")
            HOE -> itemStack.type.name.endsWith("_HOE")
        }
    }

}