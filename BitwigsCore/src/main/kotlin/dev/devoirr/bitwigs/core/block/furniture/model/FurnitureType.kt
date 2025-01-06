package dev.devoirr.bitwigs.core.block.furniture.model

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.block.furniture.model.small.FurnitureItemComparator
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class FurnitureType(val name: String, section: ConfigurationSection) {

    private val itemComparator: FurnitureItemComparator =
        BitwigsFactory.furnitureItemComparatorFactory.parse(section.getConfigurationSection("item")!!)

    val cancelDrop: Boolean = section.getBoolean("cancel-drop", false)
    val rotatable = section.getBoolean("rotatable", false)

    val allowSitting: Boolean
    val sittingYOffset: Double

    val hitbox = BitwigsFactory.hitboxFactory.parse(section.getConfigurationSection("hitbox")!!)
    val scale = BitwigsFactory.hitboxFactory.parse(section.getConfigurationSection("scale")!!)

    val breakTime =
        BitwigsFactory.perItemParameterFactory.parse(section.getConfigurationSection("break-time")!!)

    val breakDamage =
        section.getConfigurationSection("break-damage")?.let { BitwigsFactory.perItemParameterFactory.parse(it) }

    val placeEffect = section.getConfigurationSection("effects.place")?.let {
        BitwigsFactory.blockEffectFactory.parse(it)
    }

    val breakEffect = section.getConfigurationSection("effects.break")?.let {
        BitwigsFactory.blockEffectFactory.parse(it)
    }

    val hitEffect = section.getConfigurationSection("effects.hit")?.let {
        BitwigsFactory.blockEffectFactory.parse(it)
    }

    val stepEffect = section.getConfigurationSection("effects.step")?.let {
        BitwigsFactory.blockEffectFactory.parse(it)
    }

    val light = section.getInt("light", 0)

    init {
        if (section.getKeys(false).contains("sitting")) {
            allowSitting = section.getBoolean("sitting.allowed", false)
            sittingYOffset = section.getDouble("sitting.chair-y-offset", 0.0)
        } else {
            allowSitting = false
            sittingYOffset = 0.0
        }
    }

    fun isThisFurniture(itemStack: ItemStack): Boolean {
        return itemComparator.isThisItem(itemStack)
    }

}