package dev.devoirr.bitwigs.core.decoration.furniture

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.furniture.sitting.Sitting
import dev.devoirr.bitwigs.core.decoration.model.BlockEffect
import dev.devoirr.bitwigs.core.decoration.model.Hitbox
import dev.devoirr.bitwigs.core.decoration.model.Tool
import dev.devoirr.bitwigs.core.getTool
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class FurnitureType(val key: String, section: ConfigurationSection) {

    /* Item used to place this type of furniture */
    val itemComparator = BitwigsFactory.itemComparatorFactory.parse(section.getConfigurationSection("item")!!)

    /* Allow placing rotated model with SHIFT pressed. */
    val rotatable = section.getBoolean("rotatable", false)

    val sitting =
        if (section.getConfigurationSection("sitting") != null) section.get("sitting") as Sitting else null

    /* Hitbox for placing barriers. */
    val hitbox = if (section.getConfigurationSection("hitbox") != null)
        BitwigsFactory.hitboxFactory.parse(section.getConfigurationSection("hitbox")!!) else Hitbox(1.0, 1.0, 1.0)

    /* Hitbox for placing barriers. */
    val scale = if (section.getConfigurationSection("scale") != null)
        BitwigsFactory.hitboxFactory.parse(section.getConfigurationSection("scale")!!) else Hitbox(1.0, 1.0, 1.0)

    val hardness = section.getInt("hardness", 1)

    /* Prefered tool to mine the block. If null - no preference. */
    val tool =
        section.getString("tool-to-break")?.let { string -> Tool.entries.firstOrNull { it.name == string.uppercase() } }

    /* Only with those tools there will be drop. If empty - always drop. */
    val toolsToDrop = section.getStringList("tools-to-drop")

    /* List of materials that the block can be placed on. If empty - allow everywhere */
    val allowPlaceOn = section.getStringList("allow-place-on")

    /* Light level if the block. */
    val light = section.getInt("light", 0)

    /* Effects on place, break, hit and step. */
    val effects = mutableMapOf<InteractionType, BlockEffect>()

    init {
        /* Loading effects from xxx.effects section. */
        if (section.getConfigurationSection("effects") != null) {
            val effectSection = section.getConfigurationSection("effects")!!

            for (interactionType in InteractionType.entries) {
                if (effectSection.getKeys(false).contains(interactionType.name.lowercase())) {
                    effects[interactionType] =
                        BitwigsFactory.blockEffectFactory.parse(effectSection.getConfigurationSection(interactionType.name.lowercase())!!)
                }
            }
        }
    }

    fun canDrop(itemStack: ItemStack): Boolean {
        if (itemStack.type.name in toolsToDrop)
            return true

        itemStack.getTool()?.let {
            return it.name in toolsToDrop
        }

        return false
    }

    fun isThisTool(itemStack: ItemStack): Boolean {
        if (tool == null)
            return false

        itemStack.getTool()?.let {
            return tool == it
        }

        return false
    }
}
