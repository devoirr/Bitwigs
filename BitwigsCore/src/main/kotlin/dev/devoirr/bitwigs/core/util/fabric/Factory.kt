package dev.devoirr.bitwigs.core.util.fabric

import org.bukkit.configuration.ConfigurationSection

interface Factory<T> {
    fun parse(section: ConfigurationSection): T
    fun write(section: ConfigurationSection)
}