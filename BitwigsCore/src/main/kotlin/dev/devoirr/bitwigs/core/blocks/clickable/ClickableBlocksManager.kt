package dev.devoirr.bitwigs.core.blocks.clickable

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.clickable.model.ClickableBlockType
import dev.devoirr.bitwigs.core.blocks.clickable.model.database.ClickableBlocksDatabase
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.toLocation
import org.bukkit.block.Block
import org.bukkit.metadata.FixedMetadataValue
import java.io.File

class ClickableBlocksManager : Loadable {

    private val plugin = BitwigsPlugin.instance

    override fun getName(): String {
        return "clickable blocks"
    }

    val config = Config(File(plugin.dataFolder, "clickable_blocks.yml"))

    lateinit var databaseInfo: DatabaseInfo
    lateinit var database: ClickableBlocksDatabase

    private val loadedTypes = mutableMapOf<String, ClickableBlockType>()

    override fun onEnable() {

        databaseInfo =
            DatabaseInfo.parse(plugin.config.getConfigurationSection("clickable_blocks.database")!!)
        database = ClickableBlocksDatabase(this)

//        for (typeKey in config.get().getConfigurationSection("types")!!.getKeys(false)) {
//            loadedTypes[typeKey] =
//                BitwigsFactory.droppingBlockTypeFactory.parse(config.get().getConfigurationSection("types.$typeKey")!!)
//        }

        var block: Block
        database.getAll().forEach { row ->
            block = row.location.toLocation().block

            block.setMetadata("clickable_block", FixedMetadataValue(plugin, row.type))
        }

    }

    override fun onDisable() {

    }

    fun getType(name: String) = loadedTypes[name]

}