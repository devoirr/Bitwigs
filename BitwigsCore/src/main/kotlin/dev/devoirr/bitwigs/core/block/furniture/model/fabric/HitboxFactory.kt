package dev.devoirr.bitwigs.core.block.furniture.model.fabric

import dev.devoirr.bitwigs.core.block.furniture.model.small.Hitbox
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.configuration.ConfigurationSection

class HitboxFactory : Factory<Hitbox> {

    override fun parse(section: ConfigurationSection): Hitbox {
        val width = if (section.getKeys(false).contains("width")) section.getDouble("width") else 1.0
        val height = if (section.getKeys(false).contains("height")) section.getDouble("height") else 1.0
        val depth = if (section.getKeys(false).contains("depth")) section.getDouble("depth") else 1.0

        return Hitbox(width, height, depth)
    }

    override fun write(section: ConfigurationSection) {
    }
}