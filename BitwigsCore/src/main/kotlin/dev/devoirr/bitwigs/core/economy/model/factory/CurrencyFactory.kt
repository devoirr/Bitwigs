package dev.devoirr.bitwigs.core.economy.model.factory

import dev.devoirr.bitwigs.core.economy.model.Currency
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.configuration.ConfigurationSection

class CurrencyFactory : Factory<Currency> {

    override fun parse(section: ConfigurationSection): Currency {
        val key = section.name
        val global = section.getBoolean("global", false)
        val symbol = (section.getString("symbol") ?: "$").toCharArray()[0]
        val newbie = section.getDouble("newbie", 0.0)

        return Currency(key, symbol, newbie, global)
    }

    override fun write(item: Currency, section: ConfigurationSection) {
        TODO("Not yet implemented")
    }
}