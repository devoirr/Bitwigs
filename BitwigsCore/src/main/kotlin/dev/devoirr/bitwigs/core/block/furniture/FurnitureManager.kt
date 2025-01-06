package dev.devoirr.bitwigs.core.block.furniture

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.MetadataManager
import dev.devoirr.bitwigs.core.block.furniture.listener.*
import dev.devoirr.bitwigs.core.block.furniture.listener.packet.FurniturePacketListener
import dev.devoirr.bitwigs.core.block.furniture.model.FurnitureType
import dev.devoirr.bitwigs.core.block.furniture.model.database.FurnitureDatabase
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.tasks.TaskManager
import dev.devoirr.bitwigs.core.toComponent
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.io.File

class FurnitureManager(private val plugin: BitwigsPlugin) {

    private val config = Config(File(plugin.dataFolder, "furniture.yml"))
    val metadataManager = MetadataManager(plugin)

    private val furnitureTypes = mutableMapOf<String, FurnitureType>()

    private val placeListener = FurniturePlaceListener(this)
    private val breakListener = FurnitureBreakListener(this)
    private var sitListener = FurnitureSitListener(this)
    private val playerListener = FurniturePlayerListener(this)
    private val hitListener = FurnitureHitListener(this)
    private val stepListener = FurnitureStepListener(this)
    private val playerTimerListener = FurniturePlayerTimerListener(this)

    lateinit var databaseInfo: DatabaseInfo
    lateinit var furnitureDatabase: FurnitureDatabase

    val placedFurnitureHolder = PlacedFurnitureHolder(this)

    var isEnabled = false
        private set

    fun onEnable() {
        this.loadFurnitureTypes()

        databaseInfo =
            BitwigsFactory.databaseInfoFactory.parse(plugin.config.getConfigurationSection("furniture.database")!!)
        this.furnitureDatabase = FurnitureDatabase(this)

        this.placedFurnitureHolder.load()

        plugin.server.pluginManager.registerEvents(placeListener, plugin)
        plugin.server.pluginManager.registerEvents(breakListener, plugin)
        plugin.server.pluginManager.registerEvents(sitListener, plugin)
        plugin.server.pluginManager.registerEvents(playerListener, plugin)
        plugin.server.pluginManager.registerEvents(hitListener, plugin)
        plugin.server.pluginManager.registerEvents(stepListener, plugin)
//        plugin.server.pluginManager.registerEvents(playerTimerListener, plugin)

        object : BukkitRunnable() {
            override fun run() {
                placedFurnitureHolder.saveChangesToDatabase()
            }
        }.runTaskTimer(plugin, 0L, 20L * 60)

        PacketEvents.getAPI().eventManager.registerListener(FurniturePacketListener(this), PacketListenerPriority.HIGH)
        isEnabled = true
    }

    fun onDisable() {
        placedFurnitureHolder.saveChangesToDatabase()
        placedFurnitureHolder.removeSeats()

        HandlerList.unregisterAll(placeListener)
        HandlerList.unregisterAll(breakListener)
        HandlerList.unregisterAll(sitListener)
        HandlerList.unregisterAll(playerListener)
        HandlerList.unregisterAll(hitListener)
        HandlerList.unregisterAll(stepListener)

        isEnabled = false
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

    fun getFurnitureType(itemStack: ItemStack): FurnitureType? {
        return furnitureTypes.values.firstOrNull { it.isThisFurniture(itemStack) }
    }

    fun getFurnitureType(name: String) = furnitureTypes[name]

    fun getTaskManager(): TaskManager {
        return plugin.taskManager
    }

}