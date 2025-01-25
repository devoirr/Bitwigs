package dev.devoirr.bitwigs.core.decoration.furniture

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.DatabaseTable
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.toBase64
import dev.devoirr.bitwigs.core.toItemStack
import dev.devoirr.bitwigs.core.toLocation
import dev.devoirr.bitwigs.core.toString
import org.bukkit.block.BlockFace

class FurnitureDatabase(manager: FurnitureManager) {

    @DatabaseTable(tableName = "furniture")
    class PlacedFurnitureRow() {

        constructor(id: String, type: String, center: String, itemStack: String, blockFace: String) : this() {
            this.id = id
            this.type = type
            this.itemStack = itemStack
            this.center = center
            this.blockFace = blockFace
        }

        @DatabaseField(id = true, canBeNull = false)
        lateinit var id: String

        @DatabaseField(canBeNull = false)
        lateinit var type: String

        @DatabaseField(canBeNull = false)
        lateinit var center: String

        @DatabaseField(canBeNull = false)
        lateinit var itemStack: String

        @DatabaseField(canBeNull = false)
        lateinit var blockFace: String

        @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE)
        var yaw: Double = 0.0

        fun getPlacedFurniture(): FurnitureManager.PlacedFurniture {
            return FurnitureManager.PlacedFurniture(
                id,
                type,
                center.toLocation(),
                itemStack.toItemStack(),
                BlockFace.valueOf(blockFace),
                yaw.toFloat()
            )
        }

    }

    private val furnitureDao: Dao<PlacedFurnitureRow, String>

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, PlacedFurnitureRow::class.java)
        furnitureDao = DaoManager.createDao(connectionSource, PlacedFurnitureRow::class.java)
    }

    fun registerPlaced(placedFurniture: FurnitureManager.PlacedFurniture) {

        val row = PlacedFurnitureRow(
            placedFurniture.id,
            placedFurniture.type,
            placedFurniture.center.toString(true),
            placedFurniture.item.toBase64(),
            placedFurniture.blockFace.name
        )

        furnitureDao.create(row)

    }

    fun getPlacedFurniture(key: String): FurnitureManager.PlacedFurniture {
        val row = furnitureDao.queryForId(key)
        return row.getPlacedFurniture()
    }

    fun deletePlaced(id: String) {
        furnitureDao.deleteById(id)
    }

    fun getAll(): List<FurnitureManager.PlacedFurniture> {
        val list = mutableListOf<FurnitureManager.PlacedFurniture>()

        for (placedFurnitureRow in furnitureDao) {
            list.add(placedFurnitureRow.getPlacedFurniture())
        }

        return list
    }

}