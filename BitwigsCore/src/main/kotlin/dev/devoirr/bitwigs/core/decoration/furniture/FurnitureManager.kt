package dev.devoirr.bitwigs.core.decoration.furniture

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.decoration.DecorationMechanic
import dev.devoirr.bitwigs.core.decoration.HardnessModifier
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.furniture.database.FurnitureDatabase
import dev.devoirr.bitwigs.core.decoration.furniture.listener.FurniturePlaceListener
import dev.devoirr.bitwigs.core.decoration.furniture.listener.FurnitureSitListener
import dev.devoirr.bitwigs.core.decoration.furniture.listener.FurnitureStepListener
import dev.devoirr.bitwigs.core.decoration.furniture.placed.PlacedFurnitureHolder
import dev.devoirr.bitwigs.core.decoration.model.BlockEffect
import dev.devoirr.bitwigs.core.decoration.model.Tool
import dev.devoirr.bitwigs.core.toComponent
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.io.File

class FurnitureManager : DecorationMechanic {

    private val plugin = BitwigsPlugin.instance
    private val config = Config(File(plugin.dataFolder, "furniture.yml"))

    private val furnitureTypes = mutableMapOf<String, FurnitureType>()
    val furnitureDatabase = FurnitureDatabase(this)

    lateinit var databaseInfo: DatabaseInfo

    val placedFurnitureHolder = PlacedFurnitureHolder(this)

    private val placeListener = FurniturePlaceListener(this)
    private val stepListener = FurnitureStepListener(this)
    private val sitListener = FurnitureSitListener(this)

    fun onEnable() {
        this.loadFurnitureTypes()

        placedFurnitureHolder.load()

        object : BukkitRunnable() {
            override fun run() {
                placedFurnitureHolder.saveChangesToDatabase()
            }
        }.runTaskTimer(plugin, 0L, 20L * 60)

        plugin.server.pluginManager.registerEvents(placeListener, plugin)
        plugin.server.pluginManager.registerEvents(stepListener, plugin)
        plugin.server.pluginManager.registerEvents(sitListener, plugin)
    }

    fun onDisable() {
        placedFurnitureHolder.saveChangesToDatabase()
        placedFurnitureHolder.removeSeats()
    }

    private fun loadFurnitureTypes() {
        var furnitureType: FurnitureType
        for (key in config.get().getKeys(false)) {
            try {
                furnitureType = FurnitureType(key, config.get().getConfigurationSection(key)!!)
                furnitureTypes[key] = furnitureType
            } catch (e: Exception) {
                plugin.logger.info("Failed to load $key furniture type!")
                e.printStackTrace()
            }
        }
        Bukkit.getConsoleSender().sendMessage("Loaded &a${furnitureTypes.size} &ffurniture type(s).".toComponent())
    }

    fun getFurnitureType(name: String) = furnitureTypes[name]

    fun getFurnitureType(itemStack: ItemStack): FurnitureType? {
        return furnitureTypes.values.firstOrNull { it.itemComparator.isThisItem(itemStack) }
    }

    override fun isThisMechanic(block: Block): Boolean {
        return block.hasMetadata("furniture")
    }

    override fun getHardnessModifier(block: Block): HardnessModifier {
        return object : HardnessModifier {
            override fun isCalledForBlock(block: Block): Boolean {
                return isThisMechanic(block)
            }

            override fun breakBlock(block: Block) {
                if (!block.hasMetadata("furniture"))
                    return

                val id = block.getMetadata("furniture")[0].asInt()
                val placedFurniture = placedFurnitureHolder.get(id) ?: return

                placedFurnitureHolder.destroy(placedFurniture)
            }

            override fun getPeriodForBlock(block: Block, itemStack: ItemStack): Long {
                if (!block.hasMetadata("furniture"))
                    return 0L

                val id = block.getMetadata("furniture")[0].asInt()
                val placedFurniture = placedFurnitureHolder.get(id) ?: return 0L

                val type = getFurnitureType(placedFurniture.type) ?: return 0L
                val period = type.hardness
                var modifier = 1.0

                if (type.isThisTool(itemStack)) {
                    modifier *= 0.4
                    val hierarchy = Tool.getPriority(itemStack)
                    if (hierarchy >= 1) {
                        modifier *= Math.pow(0.9, hierarchy.toDouble())
                    }
                }

                return modifier.toLong() * period
            }
        }
    }

    override fun getEffect(block: Block, interactionType: InteractionType): BlockEffect? {
        if (!block.hasMetadata("furniture"))
            return null

        val id = block.getMetadata("furniture")[0].asInt()
        val placedFurniture = placedFurnitureHolder.get(id) ?: return null

        val type = getFurnitureType(placedFurniture.type) ?: return null
        return type.effects[interactionType]
    }

}