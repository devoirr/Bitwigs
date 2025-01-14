package dev.devoirr.bitwigs.core.blocks

import dev.devoirr.bitwigs.core.effect.particle.ParticleInfo
import dev.devoirr.bitwigs.core.effect.sound.SoundInfo
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

class BlockEffect(private val sound: SoundInfo?, private val particle: ParticleInfo?) {

    companion object {
        fun parse(section: ConfigurationSection): BlockEffect {
            var particle: ParticleInfo? = null
            if (section.getKeys(false).contains("particle")) {
                particle = ParticleInfo.parse(section.getConfigurationSection("particle")!!)
            }

            var sound: SoundInfo? = null
            if (section.getKeys(false).contains("sound")) {
                sound = SoundInfo.parse(section.getConfigurationSection("sound")!!)
            }

            return BlockEffect(sound, particle)
        }
    }

    fun playAt(location: Location) {
        sound?.play(location)
        particle?.playAt(location)
    }
}