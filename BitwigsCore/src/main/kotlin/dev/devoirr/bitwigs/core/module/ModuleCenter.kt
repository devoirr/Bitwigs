package dev.devoirr.bitwigs.core.module

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.action.ActionBlocksManager
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlocksManager
import dev.devoirr.bitwigs.core.chat.ChatManager
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.decoration.noteblocks.NoteblocksManager
import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.frames.FramesManager
import dev.devoirr.bitwigs.core.kits.KitsManager
import dev.devoirr.bitwigs.core.warps.WarpsManager

class ModuleCenter {

    private val plugin = BitwigsPlugin.instance

    private var economyManager: Loadable? = null
    private var warpManager: Loadable? = null
    private var chatManager: Loadable? = null
    private var droppingBlocksManager: Loadable? = null
    private var actionBlocksManager: Loadable? = null
    private var kitsManager: Loadable? = null
    private var furnitureManager: Loadable? = null
    private var noteblocksManager: Loadable? = null
    private var framesManager: Loadable? = null

    fun loadModules() {

        if (plugin.config.getBoolean("economy.enabled", false)) {
            economyManager = tryLoad(EconomyManager())
        }

        if (plugin.config.getBoolean("warps.enabled", false)) {
            warpManager = tryLoad(WarpsManager())
        }

        if (plugin.config.getBoolean("chat.enabled", false)) {
            chatManager = tryLoad(ChatManager())
        }

        if (plugin.config.getBoolean("dropping_blocks.enabled", false)) {
            droppingBlocksManager = tryLoad(DroppingBlocksManager())
        }

        if (plugin.config.getBoolean("action_blocks.enabled", false)) {
            actionBlocksManager = tryLoad(ActionBlocksManager())
        }

        if (plugin.config.getBoolean("kits.enabled", false)) {
            kitsManager = tryLoad(KitsManager())
        }

        if (plugin.config.getBoolean("furniture.enabled", false)) {
            furnitureManager = tryLoad(FurnitureManager())
        }

        if (plugin.config.getBoolean("noteblocks.enabled", false)) {
            noteblocksManager = tryLoad(NoteblocksManager())
        }

        if (plugin.config.getBoolean("frames.enabled", false)) {
            framesManager = tryLoad(FramesManager())
        }

    }

    private fun tryLoad(loadable: Loadable): Loadable? {
        try {
            loadable.onEnable()
            return loadable
        } catch (e: Exception) {
            plugin.server.logger.info("Failed to enable ${loadable.getName()}")
            e.printStackTrace()
            return null
        }
    }

}