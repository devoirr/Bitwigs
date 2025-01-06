package dev.devoirr.bitwigs.core.block.noteblocks

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.block.noteblocks.listener.NoteblockBreakListener
import dev.devoirr.bitwigs.core.block.noteblocks.listener.NoteblockInteractListener
import dev.devoirr.bitwigs.core.block.noteblocks.listener.NoteblockPlaceListener
import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockSubType
import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockType
import dev.devoirr.bitwigs.core.config.Config
import org.bukkit.block.Block
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import java.io.File

class NoteblocksManager(private val plugin: BitwigsPlugin) {

    private val loadedTypes = mutableListOf<NoteblockType>()
    private val config = Config(File(plugin.dataFolder, "noteblocks.yml"))

    /* Listeners */
    private val placeListener = NoteblockPlaceListener(this)
    private val breakListener = NoteblockBreakListener(this)
    private val interactListener = NoteblockInteractListener(this)

    fun onEnable() {
        plugin.server.pluginManager.registerEvents(placeListener, plugin)

        val section = config.get().getConfigurationSection("blocks") ?: return

        var type: NoteblockType
        for (name in section.getKeys(false)) {
            type = BitwigsFactory.noteblockTypeFactory.parse(section.getConfigurationSection(name)!!)
            loadedTypes.add(type)
        }
    }

    fun onDisable() {
        HandlerList.unregisterAll(placeListener)
        HandlerList.unregisterAll(breakListener)
        HandlerList.unregisterAll(interactListener)
    }

    fun getTypeByItemStack(itemStack: ItemStack): Pair<NoteblockType, NoteblockSubType>? {
        var subType: NoteblockSubType?
        for (type in loadedTypes) {
            subType = type.getSubTypeOf(itemStack)
            if (subType != null)
                return Pair(type, subType)
        }
        return null
    }

    fun getTypeByBlock(block: Block): Pair<NoteblockType, NoteblockSubType>? {
        var subType: NoteblockSubType?
        for (type in loadedTypes) {
            subType = type.getSubTypeOf(block)
            if (subType != null)
                return Pair(type, subType)
        }
        return null
    }

    fun getTaskManager() = plugin.taskManager

}