package dev.devoirr.bitwigs.core.decoration.furniture.database

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager

class FurnitureDatabase(manager: FurnitureManager) {

    private val furnitureDao: Dao<FurnitureDataRow, String>

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, FurnitureDataRow::class.java)
        furnitureDao = DaoManager.createDao(connectionSource, FurnitureDataRow::class.java)
    }

    fun writeFurnitureData(furnitureDataRow: FurnitureDataRow) {
        furnitureDao.createOrUpdate(furnitureDataRow)
    }

    fun removeFurnitureData(furnitureDataRow: FurnitureDataRow) {
        furnitureDao.delete(furnitureDataRow)
    }

    fun getAll(): List<FurnitureDataRow> {
        val list = mutableListOf<FurnitureDataRow>()
        for (row in furnitureDao) {
            list.add(row)
        }
        return list
    }

}