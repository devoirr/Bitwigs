package dev.devoirr.bitwigs.core.decoration.noteblocks.model

import dev.devoirr.bitwigs.core.blocks.BlockEffect
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.model.Tool
import dev.devoirr.bitwigs.core.toComponent
import org.bukkit.Instrument
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class NoteblockType(
    val key: String,
    val hardness: Int,
    private val preferredTool: Tool?,
    val toolsToDrop: List<String>,
    val effects: Map<InteractionType, BlockEffect> = mutableMapOf(),
    val light: Int,
    val subTypes: List<NoteblockSubType>
) {
    fun isThisTool(itemStack: ItemStack): Boolean {
        if (preferredTool == null)
            return false
        return preferredTool.isThisTool(itemStack)
    }

    companion object {

        fun parse(section: ConfigurationSection): NoteblockType {

            val subTypesSection = section.getConfigurationSection("blocks") ?: section.createSection("blocks")
            val subTypes = loadSubtypes(subTypesSection)

            val hardness = section.getInt("hardness", 1)
            val light = section.getInt("light", 0)

            val preferredTool =
                section.getString("preferred-tool")?.let { Tool.entries.firstOrNull { e -> e.name == it } }
            val toolsToDrop = section.getStringList("tools-to-drop")

            val effects = mutableMapOf<InteractionType, BlockEffect>()
            val effectsSection = section.getConfigurationSection("effects")
            if (effectsSection != null) {
                var type: InteractionType?
                for (interactionTypeName in effectsSection.getKeys(false)) {
                    type = InteractionType.entries.firstOrNull { it.name.lowercase() == interactionTypeName }
                    if (type == null)
                        continue

                    effects[type] =
                        BlockEffect.parse(effectsSection.getConfigurationSection(interactionTypeName)!!)
                }
            }

            return NoteblockType(section.name, hardness, preferredTool, toolsToDrop, effects, light, subTypes)
        }

        private fun loadSubtypes(section: ConfigurationSection): List<NoteblockSubType> {
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

    }

}