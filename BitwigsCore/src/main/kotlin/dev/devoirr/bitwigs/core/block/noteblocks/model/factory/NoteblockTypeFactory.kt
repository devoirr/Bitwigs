package dev.devoirr.bitwigs.core.block.noteblocks.model.factory

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockSubType
import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockType
import dev.devoirr.bitwigs.core.toComponent
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.Instrument
import org.bukkit.configuration.ConfigurationSection

class NoteblockTypeFactory : Factory<NoteblockType> {

    override fun parse(section: ConfigurationSection): NoteblockType {
        val subTypesSection = section.getConfigurationSection("blocks") ?: section.createSection("blocks")
        val subTypes = loadSubTypes(subTypesSection)

        val dropWhenBreaking = section.getStringList("drop-when-breaking")

        val breakingTimeSection = section.getConfigurationSection("breaking-time")
        val breakingTime = if (breakingTimeSection != null) {
            BitwigsFactory.perItemParameterFactory.parse(section.getConfigurationSection("breaking-time")!!)
        } else {
            dev.devoirr.bitwigs.core.block.furniture.model.small.PerItemParameter("120")
        }

        return NoteblockType(section.name, breakingTime, dropWhenBreaking, subTypes)
    }

    private fun loadSubTypes(section: ConfigurationSection): List<NoteblockSubType> {
        val list = mutableListOf<NoteblockSubType>()

        var args: List<String>
        for (modelData in section.getKeys(false)) {
            args = section.getString(modelData)!!.split(";")
            list.add(
                NoteblockSubType(
                    modelData.toInt(),
                    args[0].toInt(),
                    Instrument.valueOf(args[1]),
                    args[2].toComponent(),
                    args[3].toBoolean()
                )
            )
        }

        return list
    }

    override fun write(section: ConfigurationSection) {
        TODO("Not yet implemented")
    }
}