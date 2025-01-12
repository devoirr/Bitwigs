package dev.devoirr.bitwigs.core.effect.sound

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

class SoundInfo(private val name: String, private val volume: Float, private val pitch: Float) {

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
