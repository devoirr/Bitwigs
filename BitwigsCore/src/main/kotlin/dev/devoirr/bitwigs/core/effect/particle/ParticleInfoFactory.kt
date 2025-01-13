package dev.devoirr.bitwigs.core.effect.particle

import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

class ParticleInfoFactory : Factory<ParticleInfo?> {

    override fun parse(section: ConfigurationSection): ParticleInfo? {
        val particleName = (section.getString("particle") ?: "none").uppercase()
        val particle = Particle.entries.firstOrNull { it.name == particleName } ?: return null
            
        val amount = section.getInt("amount", 1)

        return ParticleInfo(particle, amount)
    }

    override fun write(item: ParticleInfo?, section: ConfigurationSection) {

    }
}