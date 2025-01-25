package dev.devoirr.bitwigs.core.decoration.furniture

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.BlockEffect
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.decoration.DecorationMechanic
import dev.devoirr.bitwigs.core.decoration.Hitbox
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.furniture.entities.FurnitureEntityManager
import dev.devoirr.bitwigs.core.decoration.furniture.listener.FurnitureBreakListener
import dev.devoirr.bitwigs.core.decoration.furniture.listener.FurniturePlaceListener
import dev.devoirr.bitwigs.core.decoration.furniture.listener.FurniturePlayerListener
import dev.devoirr.bitwigs.core.decoration.furniture.packet.FurniturePacketListener
import dev.devoirr.bitwigs.core.decoration.model.Tool
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.util.IdUtility
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import kotlin.math.pow

class FurnitureManager : Loadable, DecorationMechanic {

    private val plugin = BitwigsPlugin.instance
    private val types = mutableMapOf<String, FurnitureType>()
    private val config = Config(File(plugin.dataFolder, "furniture.yml"))

    private val placeListener = FurniturePlaceListener(this)
    private val breakListener = FurnitureBreakListener(this)
    private val playerListener = FurniturePlayerListener()

    private val packetListener = FurniturePacketListener(this)

    class PlacedFurniture(
        val id: String,
        val type: String,
        val center: Location,
        val item: ItemStack,
        val blockFace: BlockFace,
        val yaw: Float
    )

    lateinit var databaseInfo: DatabaseInfo
    lateinit var database: FurnitureDatabase

    val placedFurniture = mutableMapOf<String, PlacedFurniture>()

    override fun onEnable() {
        databaseInfo =
            DatabaseInfo.parse(plugin.config.getConfigurationSection("furniture.database")!!)
        database = FurnitureDatabase(this)

        config.get().getKeys(false).forEach { key ->
            config.get().getConfigurationSection(key)?.let { FurnitureType.parse(it) }.let {
                types[key] = it!!
            }
        }

        placeListener.register()
        breakListener.register()
        playerListener.register()

        PacketEvents.getAPI().eventManager.registerListener(packetListener, PacketListenerPriority.HIGH)

        object : BukkitRunnable() {
            override fun run() {
                val all = database.getAll()
                var type: FurnitureType
                var blocks: List<Block>

                for (placed in all) {
                    type = getFurnitureType(placed.type) ?: continue
                    blocks = type.hitbox.getBlocks(placed.center.block, placed.blockFace)

                    blocks.forEach {
                        it.type = Material.BARRIER
                        it.setMetadata("furniture", FixedMetadataValue(BitwigsPlugin.instance, placed.id))
                    }

                    placedFurniture[placed.id] = placed
                }
            }
        }.runTaskAsynchronously(plugin)
    }

    fun createPlacedFurniture(furniture: PlacedFurniture) {
        database.registerPlaced(furniture)
        placedFurniture[furniture.id] = furniture

        val entityLocation = furniture.center.centralize()
        entityLocation.yaw = furniture.yaw

        val entity =
            FurnitureEntityManager.FurnitureEntity(
                IdUtility.getUID(10).toInt(),
                furniture.item,
                entityLocation,
                Hitbox(1.0, 1.0, 1.0)
            )

        FurnitureEntityManager.register(furniture.id, entity)
        FurnitureEntityManager.sendToEveryone(furniture.id)
    }

    fun getPlacedFurniture(key: String): PlacedFurniture? {
        return placedFurniture[key]
    }

    fun deletePlacedFurniture(key: String) {
        database.deletePlaced(key)
        placedFurniture.remove(key)

        FurnitureEntityManager.delete(key)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(placeListener)
        HandlerList.unregisterAll(breakListener)
        HandlerList.unregisterAll(playerListener)
    }

    fun getFurnitureType(itemStack: ItemStack): FurnitureType? {
        return types.values.firstOrNull { it.comparator.isThisItem(itemStack) }
    }

    fun getFurnitureType(name: String) = types[name]

    override fun getName(): String = "furniture"

    override fun isThisMechanic(block: Block): Boolean {
        return block.hasMetadata("furniture")
    }

    override fun breakBlock(block: Block, player: Player, itemStack: ItemStack) {
        val event = BlockBreakEvent(block, player)
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getPluginManager().callEvent(event)
            }
        }.runTask(plugin)
    }

    override fun getPeriodForBlock(block: Block, itemStack: ItemStack): Long {

        val placedFurniture = getPlacedFurniture(block.getMetadata("furniture")[0].asString()) ?: return 0L
        val type = getFurnitureType(placedFurniture.type) ?: return 0L

        val period = type.hardness
        var modifier = 1.0

        if (type.isThisTool(itemStack)) {
            modifier *= 0.4
            val hierarchy = Tool.getPriority(itemStack)
            if (hierarchy >= 1) {
                modifier *= 0.9.pow(hierarchy.toDouble())
            }
        }

        return modifier.toLong() * period
    }

    override fun getEffect(block: Block, interactionType: InteractionType): BlockEffect? {
        val placedFurniture = getPlacedFurniture(block.getMetadata("furniture")[0].asString()) ?: return null
        val type = getFurnitureType(placedFurniture.type) ?: return null

        return type.effects[interactionType]
    }
}