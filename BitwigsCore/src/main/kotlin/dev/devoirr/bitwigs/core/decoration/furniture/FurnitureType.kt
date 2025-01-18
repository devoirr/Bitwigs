package dev.devoirr.bitwigs.core.decoration.furniture

import dev.devoirr.bitwigs.core.blocks.BlockEffect
import dev.devoirr.bitwigs.core.decoration.Hitbox
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.model.Tool
import dev.devoirr.bitwigs.core.item.ItemComparator
import org.bukkit.configuration.ConfigurationSection

data class FurnitureType(
    val name: String,
    val comparator: ItemComparator,
    val hitbox: Hitbox,
    val sitting: Sitting,
    val allowPlacedOn: List<String>,
    val hardness: Int,
    val preferredTool: Tool?,
    val toolsToDrop: List<String>,
    val effects: Map<InteractionType, BlockEffect> = mutableMapOf(),
    val rotatable: Boolean,
    val light: Int
) {

    companion object {

        fun parse(section: ConfigurationSection): FurnitureType? {

            val name = section.name
            val comparator = ItemComparator.parse(section.getConfigurationSection("item")!!)
            val hitbox = Hitbox.parse(section.getConfigurationSection("hitbox")!!)
            val sitting = section.getConfigurationSection("sitting")?.let { Sitting.parse(it) } ?: Sitting(false)
            val rotatable = section.getBoolean("rotatable", false)
            val allowPlacedOn = section.getStringList("allow-place-on")
            val hardness = section.getInt("hardness", 1)

            val preferredTool =
                section.getString("preferred-tool")?.let { Tool.entries.firstOrNull { e -> e.name == it } }
            val toolsToDrop = section.getStringList("tools-to-drop")

            return FurnitureType(
                name,
                comparator,
                hitbox,
                sitting,
                allowPlacedOn,
                hardness,
                preferredTool,
                toolsToDrop,
                mutableMapOf(),
                rotatable,
                1
            )
        }

    }

    class Sitting(val allowed: Boolean, val yOffset: Double = 0.0) {
        companion object {
            fun parse(section: ConfigurationSection): Sitting {
                return Sitting(section.getBoolean("allowed", false), section.getDouble("chair-y-offset", 0.0))
            }
        }
    }

}