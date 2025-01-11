package dev.devoirr.bitwigs.core.frames

import org.bukkit.Location
import org.bukkit.inventory.ItemStack

data class FrameData(
    val id: Int,
    val location: Location,
    val direction: Int,
    var rotation: Int,
    var itemStack: ItemStack?
)