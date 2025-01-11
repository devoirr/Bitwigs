package dev.devoirr.bitwigs.core.blocks

import dev.devoirr.bitwigs.core.effect.particle.ParticleInfo
import dev.devoirr.bitwigs.core.effect.sound.SoundInfo
import org.bukkit.Location

class BlockEffect(private val sound: SoundInfo?, private val particle: ParticleInfo?) {

    fun playAt(location: Location) {
        sound?.play(location)
        particle?.playAt(location)
    }
}