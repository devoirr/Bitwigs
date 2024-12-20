package dev.devoirr.bitwigs.core

import co.aikar.commands.PaperCommandManager
import dev.devoirr.bitwigs.core.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.gui.listener.MenuListener
import org.bukkit.plugin.java.JavaPlugin

class BitwigsPlugin : JavaPlugin() {

    private lateinit var commandManager: PaperCommandManager

    private lateinit var furnitureManager: FurnitureManager

    override fun onEnable() {
        print("Enabled Bitwigs!")

        commandManager = PaperCommandManager(this)

        furnitureManager = FurnitureManager(this)
        furnitureManager.onEnable()

        server.pluginManager.registerEvents(MenuListener(), this)
    }

    override fun onDisable() {
        furnitureManager.onDisable()
    }

}