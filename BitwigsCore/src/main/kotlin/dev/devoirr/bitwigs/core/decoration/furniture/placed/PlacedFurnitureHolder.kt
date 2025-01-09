package dev.devoirr.bitwigs.core.decoration.furniture.placed

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.decoration.furniture.database.FurnitureDataRow
import dev.devoirr.bitwigs.core.toLocation
import dev.devoirr.bitwigs.core.toString
import org.bukkit.Chunk
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class PlacedFurnitureHolder(private val manager: FurnitureManager) {

    private val loadedFurniture = mutableMapOf<Int, PlacedFurniture>()
    private val addQueue: Queue<FurnitureDataRow> = LinkedList()
    private val removeQueue: Queue<FurnitureDataRow> = LinkedList()

    fun saveChangesToDatabase() {
        var dataRow: FurnitureDataRow
        while (removeQueue.isNotEmpty()) {
            dataRow = removeQueue.remove()
            manager.furnitureDatabase.removeFurnitureData(dataRow)
        }
        while (addQueue.isNotEmpty()) {
            dataRow = addQueue.remove()
            manager.furnitureDatabase.writeFurnitureData(dataRow)
        }
    }

    fun load() {
        val rows = manager.furnitureDatabase.getAll()
        var furniture: PlacedFurniture
        for (row in rows) {
            furniture = PlacedFurniture(
                manager,
                row.type,
                row.center.toLocation().block,
                BlockFace.valueOf(row.facing),
                row.yaw.toFloat(),
                row.display,
                row.itemStack
            )
            furniture.updateMetadata(manager)
            furniture.sendCreationForWorld()
            loadedFurniture[furniture.display] = furniture
        }
    }

    fun register(furniture: PlacedFurniture) {
        loadedFurniture[furniture.display] = furniture
        removeQueue.removeAll { it.display == furniture.display }
        val row = createDataRow(furniture)
        addQueue.add(row)
    }

    fun destroy(furniture: PlacedFurniture) {
        furniture.destroy(manager)
        loadedFurniture.remove(furniture.display)
        addQueue.removeAll { it.display == furniture.display }
        val row = createDataRow(furniture)
        removeQueue.add(row)
    }

    fun get(id: Int) = loadedFurniture[id]

    private fun createDataRow(placedFurniture: PlacedFurniture): FurnitureDataRow {
        val databaseRow = FurnitureDataRow()

        databaseRow.facing = placedFurniture.facing.name
        databaseRow.type = placedFurniture.type
        databaseRow.display = placedFurniture.display
        databaseRow.yaw = placedFurniture.yaw.toDouble()
        databaseRow.itemStack = placedFurniture.base64
        databaseRow.center = placedFurniture.center.location.toString(block = true)

        return databaseRow
    }

    fun removeSeats() {
        this.loadedFurniture.values.forEach { placedFurniture ->
            placedFurniture.seats.forEach { it.remove() }
        }
    }

    fun createId(): Int {
        return System.currentTimeMillis().toInt()
    }

    fun sendAllForPlayer(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                loadedFurniture.values
                    .forEach {
                        it.sendCreationForPlayer(player)
                    }
            }
        }.runTaskLaterAsynchronously(BitwigsPlugin.instance, 20L)
    }

    fun sendChunkForPlayer(player: Player, chunk: Chunk) {
        object : BukkitRunnable() {
            override fun run() {
                loadedFurniture.values.filter { it.center.chunk.chunkKey == chunk.chunkKey }
                    .forEach {
                        it.sendCreationForPlayer(player)
                    }
            }
        }.runTaskLaterAsynchronously(BitwigsPlugin.instance, 1L)
    }
}