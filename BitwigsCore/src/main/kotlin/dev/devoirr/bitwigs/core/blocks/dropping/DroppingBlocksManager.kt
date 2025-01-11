package dev.devoirr.bitwigs.core.blocks.dropping

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.dropping.command.DroppingBlocksCommand
import dev.devoirr.bitwigs.core.blocks.dropping.model.DroppingBlockType
import dev.devoirr.bitwigs.core.blocks.dropping.model.database.DroppingBlocksDatabase
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.toLocation
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

    override fun onEnable() {
        databaseInfo =
            BitwigsFactory.databaseInfoFactory.parse(plugin.config.getConfigurationSection("dropping_blocks.database")!!)
        database = DroppingBlocksDatabase(this)

        for (typeKey in config.get().getConfigurationSection("types")!!.getKeys(false)) {
            loadedTypes[typeKey] =
                BitwigsFactory.droppingBlockTypeFactory.parse(config.get().getConfigurationSection("types.$typeKey")!!)
        }

        database.getAll().forEach { row ->
            row.location.toLocation().block.setMetadata("dropping_block", FixedMetadataValue(plugin, row.type))
        }

        plugin.commandManager.registerCommand(DroppingBlocksCommand(this))
    }

    override fun onDisable() {
    }

    fun getType(typeName: String): DroppingBlockType? {
        return loadedTypes[typeName]
    }
}