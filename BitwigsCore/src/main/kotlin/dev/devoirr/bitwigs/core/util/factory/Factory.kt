package dev.devoirr.bitwigs.core.util.factory

import org.bukkit.configuration.ConfigurationSection

interface Factory<T> {
    fun parse(section: ConfigurationSection): T
    fun write(item: T, section: ConfigurationSection)
}