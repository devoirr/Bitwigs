package dev.devoirr.bitwigs.core.blocks.dropping.model.database

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlocksManager

class DroppingBlocksDatabase(manager: DroppingBlocksManager) {

    private val droppingBlockDao: Dao<PlacedDroppingBlockRow, String>

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, PlacedDroppingBlockRow::class.java)
        droppingBlockDao = DaoManager.createDao(connectionSource, PlacedDroppingBlockRow::class.java)
    }

    fun write(row: PlacedDroppingBlockRow) {
        droppingBlockDao.create(row)
    }

    fun delete(row: PlacedDroppingBlockRow) {
        droppingBlockDao.delete(row)
    }

    fun getAll(): List<PlacedDroppingBlockRow> {
        return droppingBlockDao.queryForAll()
    }

}