package dev.devoirr.bitwigs.core.furniture.model.fabric

import dev.devoirr.bitwigs.core.furniture.model.PerItemParameter
import dev.devoirr.bitwigs.core.util.fabric.Factory
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

class PerItemParameterFactory : Factory<PerItemParameter> {

    override fun parse(section: ConfigurationSection): PerItemParameter {

        val default: String = section.getString("default") ?: "0"
        val perItemParameter = PerItemParameter(default)

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