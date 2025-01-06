package dev.devoirr.bitwigs.core.block.furniture.model.fabric

import dev.devoirr.bitwigs.core.block.furniture.model.small.BlockEffect
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.configuration.ConfigurationSection

class BlockEffectFactory : Factory<BlockEffect> {

    override fun parse(section: ConfigurationSection): BlockEffect {
        val particleString = section.getString("particle")
        val soundString = section.getString("sound")

        var particle: Particle? = null
        var sound: Sound? = null

        particleString?.let {
            particle = Particle.entries.firstOrNull { it.name == particleString }
        }

        soundString?.let {
            sound = Sound.entries.firstOrNull { it.name == soundString }
        }

        return BlockEffect(sound, particle)
    }

    override fun write(section: ConfigurationSection) {

    }
}