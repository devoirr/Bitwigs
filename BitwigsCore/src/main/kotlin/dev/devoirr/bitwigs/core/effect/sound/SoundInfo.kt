package dev.devoirr.bitwigs.core.effect.sound

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class SoundInfo(private val name: String, private val volume: Float, private val pitch: Float) {

    companion object {
        fun parse(section: ConfigurationSection): SoundInfo {
            val soundName = (section.getString("sound") ?: "none")
            val volume = section.getDouble("volume", 1.0)
            val pitch = section.getDouble("pitch", 1.0)

            return SoundInfo(soundName, volume.toFloat(), pitch.toFloat())
        }
    }

    fun play(player: Player) {
        try {
            player.playSound(player, name, volume, pitch)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(location: Location) {
        try {
            location.world.playSound(location, name, volume, pitch)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(block: Block) {
        play(block.location)
    }

}
