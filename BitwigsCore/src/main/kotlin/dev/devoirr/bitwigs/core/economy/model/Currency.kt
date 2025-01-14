package dev.devoirr.bitwigs.core.economy.model

import org.bukkit.configuration.ConfigurationSection

data class Currency(val key: String, val symbol: Char, val newbie: Double, val global: Boolean) {
    companion object {
        fun parse(section: ConfigurationSection): Currency {
            val key = section.name
            val global = section.getBoolean("global", false)
            val symbol = (section.getString("symbol") ?: "$").toCharArray()[0]
            val newbie = section.getDouble("newbie", 0.0)

            return Currency(key, symbol, newbie, global)
        }
    }
}
