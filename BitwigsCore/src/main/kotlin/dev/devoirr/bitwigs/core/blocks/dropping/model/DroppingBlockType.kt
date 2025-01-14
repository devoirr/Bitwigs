package dev.devoirr.bitwigs.core.blocks.dropping.model

import dev.devoirr.bitwigs.core.BitwigsServices
import dev.devoirr.bitwigs.core.blocks.BlockEffect
import dev.devoirr.bitwigs.core.blocks.ReplacerInfo
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection

data class DroppingBlockType(
    val lootTime: Int,
    val refillTime: Int,
    val refillAfterLoots: Int,
    val lootEffect: BlockEffect?,
    val refillEffect: BlockEffect?,
    val canBeBroken: Boolean,
    val lootBars: Int,
    val refillBars: Int,
    val hologramYOffset: Double,
    val items: List<DroppingBlockItem>,
    val lootFiller: ReplacerInfo?,
    val refillFiller: ReplacerInfo?
) {

    companion object {
        fun parse(section: ConfigurationSection): DroppingBlockType {
            val lootTime = section.getInt("loot_time", 30)
            val refillTime = section.getInt("refill_time", 30)
            val refillAfterLoots = section.getInt("refill_after_loots", 5)

            val refillEffect = if (section.getKeys(false).contains("refill_effect"))
                BlockEffect.parse(section.getConfigurationSection("refill_effect")!!) else null

            val lootEffect = if (section.getKeys(false).contains("loot_effect"))
                BlockEffect.parse(section.getConfigurationSection("loot_effect")!!) else null

            val canBeBroken = section.getBoolean("can_be_broken", false)
            val lootBars = section.getInt("loot_bars", 10)
            val refillBars = section.getInt("refill_bars", 10)

            val yOffset = section.getDouble("hologram_y_offset", 0.8)

            val lootFiller =
                if (section.getKeys(false)
                        .contains("loot_filler")
                ) ReplacerInfo.createFromSection(section.getConfigurationSection("loot_filler")!!) else null

            val refillFiller =
                if (section.getKeys(false)
                        .contains("refill_filler")
                ) ReplacerInfo.createFromSection(section.getConfigurationSection("refill_filler")!!) else null

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
                    emptyList(),
                    lootFiller,
                    refillFiller
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
                        chance = section.getDouble("items.$itemId", 0.0)
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
                items,
                lootFiller,
                refillFiller
            )
        }
    }

}
