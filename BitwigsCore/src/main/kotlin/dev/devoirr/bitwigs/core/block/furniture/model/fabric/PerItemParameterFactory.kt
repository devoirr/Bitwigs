package dev.devoirr.bitwigs.core.block.furniture.model.fabric

import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class PerItemParameterFactory : Factory<dev.devoirr.bitwigs.core.block.furniture.model.small.PerItemParameter> {

    override fun parse(section: ConfigurationSection): dev.devoirr.bitwigs.core.block.furniture.model.small.PerItemParameter {

        val default: String = section.getString("default") ?: "0"
        val perItemParameter = dev.devoirr.bitwigs.core.block.furniture.model.small.PerItemParameter(default)

        val perItemSection = section.getConfigurationSection("per-item")
        perItemSection?.let {
            for (key in perItemSection.getKeys(false)) {
                val material = Material.matchMaterial(key.uppercase())
                material?.let {
                    perItemParameter.add(material, perItemSection.getString(key)!!)
                }
            }
        }

        return perItemParameter

    }

    override fun write(section: ConfigurationSection) {
    }
}