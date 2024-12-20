package dev.devoirr.bitwigs.core.furniture

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.furniture.model.FurnitureType
import org.bukkit.Bukkit
import java.io.File

class FurnitureManager(private val plugin: BitwigsPlugin) {

    private val config = Config(File(plugin.dataFolder, "furniture.yml"))

    private val furnitureTypes = mutableMapOf<String, FurnitureType>()

    fun onEnable() {

        var furnitureType: FurnitureType
        for (key in config.get().getKeys(false)) {
            try {
                furnitureType = FurnitureType(config.get().getConfigurationSection(key)!!)
                furnitureTypes[key] = furnitureType
            } catch (e: Exception) {
                Bukkit.getLogger().info("Failed to load $key furniture type!")
                e.printStackTrace()
            }
        }

        Bukkit.getLogger().info("Loaded ${furnitureTypes.size} furniture types.")

    }

    fun onDisable() {

    }

}