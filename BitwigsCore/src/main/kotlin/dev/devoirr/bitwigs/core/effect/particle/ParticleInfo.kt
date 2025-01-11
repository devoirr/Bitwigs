package dev.devoirr.bitwigs.core.effect.particle

import org.bukkit.Location
import org.bukkit.Particle

class ParticleInfo(private val particle: Particle, private val amount: Int) {

    fun playAt(location: Location) {
        location.world.spawnParticle(particle, location, amount)
    }
}