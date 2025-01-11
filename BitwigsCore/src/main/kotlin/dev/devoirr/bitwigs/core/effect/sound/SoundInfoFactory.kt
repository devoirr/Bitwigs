package dev.devoirr.bitwigs.core.effect.sound

import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.configuration.ConfigurationSection

class SoundInfoFactory : Factory<SoundInfo> {

    override fun parse(section: ConfigurationSection): SoundInfo {
        val soundName = (section.getString("sound") ?: "none").uppercase()
        val volume = section.getDouble("volume", 1.0)
        val pitch = section.getDouble("pitch", 1.0)

        return SoundInfo(soundName, volume.toFloat(), pitch.toFloat())
    }

    override fun write(item: SoundInfo, section: ConfigurationSection) {
        TODO("Not yet implemented")
    }
}