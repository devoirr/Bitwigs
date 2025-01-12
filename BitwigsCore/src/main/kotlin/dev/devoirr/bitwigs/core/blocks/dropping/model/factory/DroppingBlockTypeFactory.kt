package dev.devoirr.bitwigs.core.blocks.dropping.model.factory

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsServices
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockItem
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockType
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

        val canBeBroken = section.getBoolean("can_be_broken", false)
        val lootBars = section.getInt("loot_bars", 10)
        val refillBars = section.getInt("refill_bars", 10)

        val yOffset = section.getDouble("hologram_y_offset", 0.8)

        val items = mutableListOf<DroppingBlockItem>()
        if (!section.getKeys(false).contains("items")) {
            return DroppingBlockType(
                lootTime,
                refillTime,
                refillAfterLoots,
                lootEffect,
                refillEffect,
                canBeBroken,
                lootBars,
                refillBars,
                yOffset,
                emptyList()
            )
        }

        try {
            val areDefaultItems = !section.isConfigurationSection("items")
            if (areDefaultItems) {

                val listOfItemIds = section.getStringList("items")
                for (itemName in listOfItemIds) {
                    BitwigsServices.droppingBlocksSerivce?.getItem(itemName)?.let { items.add(it) }
                }

            } else {

                var chance: Double
                for (itemId in section.getConfigurationSection("items")!!.getKeys(false)) {
                    chance = section.getDouble("items.$itemId")!!
                    BitwigsServices.droppingBlocksSerivce?.getItem(itemId)
                        ?.let { items.add(it.copyWithChange(chance)) }
                }

            }
        } catch (e: Exception) {
            Bukkit.getLogger().info("Failed to load items for ${section.name}")
            e.printStackTrace()
        }

        return DroppingBlockType(
            lootTime,
            refillTime,
            refillAfterLoots,
            lootEffect,
            refillEffect,
            canBeBroken,
            lootBars,
            refillBars,
            yOffset,
            items
        )
    }

    override fun write(item: DroppingBlockType, section: ConfigurationSection) {
        TODO("Not yet implemented")
    }
}