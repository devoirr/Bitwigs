package dev.devoirr.bitwigs.core.blocks.dropping

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.BitwigsServices
import dev.devoirr.bitwigs.core.blocks.dropping.command.DroppingBlocksCommand
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockItem
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockType
import dev.devoirr.bitwigs.core.blocks.dropping.model.database.DroppingBlocksDatabase
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.toLocation
import org.bukkit.block.Block
import org.bukkit.event.HandlerList
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

class DroppingBlocksManager : Loadable {

    override fun getName(): String {
        return "dropping blocks"
    }

    private val plugin = BitwigsPlugin.instance
    val config = Config(File(plugin.dataFolder, "dropping_blocks.yml"))

    lateinit var databaseInfo: DatabaseInfo
    lateinit var database: DroppingBlocksDatabase

    private val loadedTypes = mutableMapOf<String, DroppingBlockType>()

    private val listener = DroppingBlocksListener(this)
    private val items = mutableMapOf<String, DroppingBlockItem>()

    override fun onEnable() {
        databaseInfo =
            DatabaseInfo.parse(plugin.config.getConfigurationSection("dropping_blocks.database")!!)
        database = DroppingBlocksDatabase(this)

        config.get().getConfigurationSection("items")?.getKeys(false)?.forEach { key ->
            items[key] =
                DroppingBlockItem.parse(config.get().getConfigurationSection("items.$key")!!)
        }

        BitwigsServices.droppingBlocksSerivce = object : DroppingBlocksSerivce {
            override fun getItem(id: String): DroppingBlockItem? {
                return items[id]
            }
        }

        for (typeKey in config.get().getConfigurationSection("types")!!.getKeys(false)) {
            loadedTypes[typeKey] =
                DroppingBlockType.parse(config.get().getConfigurationSection("types.$typeKey")!!)
        }

        var block: Block
        database.getAll().forEach { row ->
            block = row.location.toLocation().block

            block.setMetadata("dropping_block", FixedMetadataValue(plugin, row.type))
            block.setMetadata("dropping_block_loots", FixedMetadataValue(plugin, 0))
        }

        plugin.commandManager.registerCommand(DroppingBlocksCommand(this))
        plugin.commandManager.commandCompletions.registerCompletion("droppingblocktypes") {
            loadedTypes.keys
        }

        plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(listener)
    }

    fun getType(typeName: String): DroppingBlockType? {
        return loadedTypes[typeName]
    }
}