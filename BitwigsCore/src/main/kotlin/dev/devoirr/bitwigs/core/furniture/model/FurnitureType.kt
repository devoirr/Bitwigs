package dev.devoirr.bitwigs.core.furniture.model

import dev.devoirr.bitwigs.core.BitwigsFactory
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class FurnitureType(section: ConfigurationSection) {

    private val itemComparator: FurnitureItemComparator =
        BitwigsFactory.furnitureItemComparatorFactory.parse(section.getConfigurationSection("item")!!)
    
    private val cancelDrop: Boolean = section.getBoolean("cancel-drop", false)
    private val rotatable = section.getBoolean("rotatable", false)

    private val allowSitting: Boolean
    private val sittingYOffset: Double

    private val exposive: Boolean
    private val exposionPower: Int
    private val exposionTime: Int

    private val hitbox = BitwigsFactory.hitboxFactory.parse(section.getConfigurationSection("hitbox")!!)
    private val transformation = section.getConfigurationSection("transformation")
        ?.let { BitwigsFactory.hitboxFactory.parse(it) }

    private val breakTime =
        BitwigsFactory.perItemParameterFactory.parse(section.getConfigurationSection("break-time")!!)

    private val breakDamage =
        section.getConfigurationSection("break-damage")?.let { BitwigsFactory.perItemParameterFactory.parse(it) }

    private val placeEffect = section.getConfigurationSection("effects.place")?.let {
        BitwigsFactory.blockEffectFactory.parse(it)
    }

    private val breakEffect = section.getConfigurationSection("effects.break")?.let {
        BitwigsFactory.blockEffectFactory.parse(it)
    }

    private val hitEffect = section.getConfigurationSection("effects.hit")?.let {
        BitwigsFactory.blockEffectFactory.parse(it)
    }

    init {
        if (section.getKeys(false).contains("sitting")) {
            allowSitting = section.getBoolean("sitting.allowed", false)
            sittingYOffset = section.getDouble("sitting.chair-y-offset", 0.0)
        } else {
            allowSitting = false
            sittingYOffset = 0.0
        }

        if (section.getKeys(false).contains("explosive")) {
            exposive = section.getBoolean("explosive.allowed", false)
            exposionTime = section.getInt("explosive.time", 100)
            exposionPower = section.getInt("explosive.power", 1)
        } else {
            exposive = false
            exposionPower = 0
            exposionTime = 0
        }
    }

    fun isThisFurniture(itemStack: ItemStack): Boolean {
        return itemComparator.isThisItem(itemStack)
    }

}