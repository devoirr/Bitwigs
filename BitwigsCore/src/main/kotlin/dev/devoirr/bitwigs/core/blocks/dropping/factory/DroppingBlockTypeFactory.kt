package dev.devoirr.bitwigs.core.blocks.dropping.factory

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsServices
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlockItem
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlockType
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

class DroppingBlockTypeFactory : Factory<DroppingBlockType> {

    override fun parse(section: ConfigurationSection): DroppingBlockType {
        val lootTime = section.getInt("loot_time", 30)
        val refillTime = section.getInt("refill_time", 30)
        val refillAfterLoots = section.getInt("refill_after_loots", 5)

        val refillEffect = if (section.getKeys(false).contains("refill_effect"))
            BitwigsFactory.blockEffectFactory.parse(section.getConfigurationSection("refill_effect")!!) else null

        val lootEffect = if (section.getKeys(false).contains("loot_effect"))
            BitwigsFactory.blockEffectFactory.parse(section.getConfigurationSection("loot_effect")!!) else null

        val items = mutableListOf<DroppingBlockItem>()
        if (!section.getKeys(false).contains("items")) {
            return DroppingBlockType(lootTime, refillTime, refillAfterLoots, lootEffect, refillEffect, emptyList())
        }

        try {
            val areDefaultItems = !section.isConfigurationSection("items")
            if (areDefaultItems) {

                val listOfItemIds = section.getStringList("items")
                for (itemName in listOfItemIds) {
                    BitwigsServices.droppingBlocksSerivce?.getItem(itemName)?.let { items.add(it) }
                }

            } else {

                var itemId: String
                for (chance in section.getConfigurationSection("items")!!.getKeys(false)) {
                    itemId = section.getString("items.$chance")!!
                    BitwigsServices.droppingBlocksSerivce?.getItem(itemId)
                        ?.let { items.add(it.copyWithChange(chance.toDouble())) }
                }

            }
        } catch (e: Exception) {
            Bukkit.getLogger().info("Failed to load items for ${section.name}")
            e.printStackTrace()
        }

        return DroppingBlockType(lootTime, refillTime, refillAfterLoots, lootEffect, refillEffect, items)
    }

    override fun write(item: DroppingBlockType, section: ConfigurationSection) {
        TODO("Not yet implemented")
    }
}