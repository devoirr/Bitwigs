package dev.devoirr.bitwigs.core.blocks.dropping

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.dropping.command.DroppingBlocksCommand
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.module.Loadable
import java.io.File

class DroppingBlocksManager : Loadable {

    override fun getName(): String {
        return "dropping blocks"
    }

    private val plugin = BitwigsPlugin.instance
    val config = Config(File(plugin.dataFolder, "dropping_blocks.yml"))

    override fun onEnable() {
        plugin.commandManager.registerCommand(DroppingBlocksCommand(this))
    }

    override fun onDisable() {
    }
}