package dev.devoirr.bitwigs.core.sound

import org.bukkit.block.Block
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.entity.Player

class SoundInfo(map: Map<String, Any>) : ConfigurationSerializable {

    private var name: String = map["sound"]!!.toString()
    private var volume: Float = map["volume"]!!.toString().toFloat()
    private var pitch: Float = map["pitch"]!!.toString().toFloat()

    constructor(name: String, volume: Float, pitch: Float) : this(
        mapOf(
            "sound" to name,
            "volume" to volume,
            "pitch" to pitch
        )
    )

    fun play(player: Player) {
        player.playSound(player, name, volume, pitch)
    }

    fun play(block: Block) {
        block.world.playSound(block.location, name, volume, pitch)
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("sound" to name, "volume" to volume, "pitch" to pitch)
    }
}
