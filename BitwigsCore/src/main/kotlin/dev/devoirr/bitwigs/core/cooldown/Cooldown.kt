package dev.devoirr.bitwigs.core.cooldown

import org.bukkit.configuration.ConfigurationSection
import java.util.*

data class Cooldown(val defaultTime: Int, val groups: Map<String, Int>, val message: String?) {

    val key: String = UUID.randomUUID().toString().split("-")[1]

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

    fun getFor(group: String): Int {
        return groups.getOrDefault(group, defaultTime)
    }
}