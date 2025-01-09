package dev.devoirr.bitwigs.core.decoration.model.factory

import dev.devoirr.bitwigs.core.decoration.model.BlockEffect
import dev.devoirr.bitwigs.core.sound.SoundInfo
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

class BlockEffectFactory : Factory<BlockEffect> {

    override fun parse(section: ConfigurationSection): BlockEffect {

        val particleString = section.getString("particle")
        var particle: Particle? = null
        particleString?.let {
            particle = Particle.entries.firstOrNull { it.name == particleString }
        }

        var soundInfo: SoundInfo? = null
        if (section.getKeys(false).contains("sound")) {
            soundInfo = section.get("sound") as SoundInfo
        }

        return BlockEffect(soundInfo, particle)
    }

    override fun write(section: ConfigurationSection) {
        TODO("Not yet implemented")
    }
}