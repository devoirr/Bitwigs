package dev.devoirr.bitwigs.core.blocks.action

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.action.model.ActionBlockType
import dev.devoirr.bitwigs.core.blocks.action.model.database.ActionBlocksDatabase
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.toLocation
import org.bukkit.block.Block
import org.bukkit.event.HandlerList
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

class ActionBlocksManager : Loadable {

    private val plugin = BitwigsPlugin.instance

    override fun getName(): String {
        return "action blocks"
    }

    val config = Config(File(plugin.dataFolder, "action_blocks.yml"))

    lateinit var databaseInfo: DatabaseInfo
    lateinit var database: ActionBlocksDatabase

    private val loadedTypes = mutableMapOf<String, ActionBlockType>()
    private val listener = ActionBlockListener(this)

    override fun onEnable() {

        databaseInfo =
            DatabaseInfo.parse(plugin.config.getConfigurationSection("action_blocks.database")!!)
        database = ActionBlocksDatabase(this)

        for (typeKey in config.get().getConfigurationSection("types")!!.getKeys(false)) {
            loadedTypes[typeKey] =
                ActionBlockType.parse(config.get().getConfigurationSection("types.$typeKey")!!)
        }

        var block: Block
        database.getAll().forEach { row ->
            block = row.location.toLocation().block

            block.setMetadata("action_block", FixedMetadataValue(plugin, row.type))
        }

        plugin.commandManager.registerCommand(ActionBlockCommand(this))
        plugin.commandManager.commandCompletions.registerCompletion("actionblocktypes") {
            loadedTypes.keys
        }

        listener.register()

    }

    override fun onDisable() {
        HandlerList.unregisterAll(listener)
    }

    fun getType(name: String) = loadedTypes[name]

}