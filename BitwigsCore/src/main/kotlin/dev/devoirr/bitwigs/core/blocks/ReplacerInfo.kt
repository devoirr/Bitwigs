package dev.devoirr.bitwigs.core.blocks

import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection

data class ReplacerInfo(val material: Material, val note: Int?, val instrument: Instrument?, val isPowered: Boolean?) {
    companion object {
        fun createFromSection(section: ConfigurationSection): ReplacerInfo {
            val material = Material.matchMaterial(section.getString("material") ?: "STONE") ?: Material.STONE

            var note: Int? = null
            var instrument: Instrument? = null
            var isPowered: Boolean? = null

            if (section.getKeys(false).contains("note")) {
                note = section.getInt("note", 0).coerceAtLeast(0).coerceAtMost(8)
            }

            if (section.getKeys(false).contains("instrument")) {
                instrument =
                    section.getString("instrument")?.let { name -> Instrument.entries.firstOrNull { it.name == name } }
            }

            if (section.getKeys(false).contains("powered")) {
                isPowered = if (section.isBoolean("powered")) section.getBoolean("powered", false) else false
            }

            return ReplacerInfo(material, note, instrument, isPowered)
        }
    }
}
