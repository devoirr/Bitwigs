package dev.devoirr.bitwigs.core

import co.aikar.commands.PaperCommandManager
import dev.devoirr.bitwigs.core.gui.listener.MenuListener
import org.bukkit.plugin.java.JavaPlugin

class BitwigsPlugin : JavaPlugin() {

    private lateinit var commandManager: PaperCommandManager

    override fun onEnable() {
        print("Enabled Bitwigs!")

        commandManager = PaperCommandManager(this)

        server.pluginManager.registerEvents(MenuListener(), this)
    }

}