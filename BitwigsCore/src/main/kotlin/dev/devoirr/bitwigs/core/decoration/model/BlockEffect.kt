package dev.devoirr.bitwigs.core.decoration.model

import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.sound.SoundInfo
import org.bukkit.Particle
import org.bukkit.block.Block

data class BlockEffect(val sound: SoundInfo?, val particle: Particle?) {

    fun play(block: Block) {
        sound?.play(block)
        particle?.let {
            block.world.spawnParticle(particle, block.location.centralize(), 10)
        }
    }

}