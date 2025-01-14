package dev.devoirr.bitwigs.core.effect.particle

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.configuration.ConfigurationSection

class ParticleInfo(private val particle: Particle, private val amount: Int) {

    companion object {
        fun parse(section: ConfigurationSection): ParticleInfo? {
            val particleName = (section.getString("particle") ?: "none").uppercase()
            val particle = Particle.entries.firstOrNull { it.name == particleName } ?: return null

            val amount = section.getInt("amount", 1)

            return ParticleInfo(particle, amount)
        }
    }

    fun playAt(location: Location) {
        location.world.spawnParticle(particle, location, amount, 0.0, 0.0, 0.0)
    }
}