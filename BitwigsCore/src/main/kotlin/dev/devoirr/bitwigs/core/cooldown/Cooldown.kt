package dev.devoirr.bitwigs.core.cooldown

import org.bukkit.configuration.ConfigurationSection

data class Cooldown(val defaultTime: Int, val groups: Map<String, Int>, private val message: String?) {
    companion object {
        fun parse(section: ConfigurationSection): Cooldown {
            val default = section.getInt("default", 0)
            val groups = mutableMapOf<String, Int>()

            if (section.getKeys(false).contains("groups")) {
                section.getConfigurationSection("groups")?.getKeys(false)?.forEach {
                    groups[it] = section.getInt("groups.$it", 0)
                }
            }

            val message = section.getString("message")
            return Cooldown(default, groups, message)
        }
    }
}