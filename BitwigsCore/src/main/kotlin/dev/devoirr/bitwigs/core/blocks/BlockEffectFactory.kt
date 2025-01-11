package dev.devoirr.bitwigs.core.blocks

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.effect.particle.ParticleInfo
import dev.devoirr.bitwigs.core.effect.sound.SoundInfo
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.configuration.ConfigurationSection

class BlockEffectFactory : Factory<BlockEffect> {

    override fun parse(section: ConfigurationSection): BlockEffect {
        var particle: ParticleInfo? = null
        if (section.getKeys(false).contains("particle")) {
            particle = BitwigsFactory.particleInfoFactory.parse(section.getConfigurationSection("particle")!!)
        }

        var sound: SoundInfo? = null
        if (section.getKeys(false).contains("sound")) {
            sound = BitwigsFactory.soundInfoFactory.parse(section.getConfigurationSection("sound")!!)
        }

        return BlockEffect(sound, particle)
    }

    override fun write(item: BlockEffect, section: ConfigurationSection) {

    }
}