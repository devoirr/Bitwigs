package dev.devoirr.bitwigs.core.blocks.action.model.database

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.blocks.action.ActionBlocksManager

class ActionBlocksDatabase(manager: ActionBlocksManager) {

    private val clickableBlocksDao: Dao<PlacedActionBlockRow, String>

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, PlacedActionBlockRow::class.java)
        clickableBlocksDao = DaoManager.createDao(connectionSource, PlacedActionBlockRow::class.java)
    }

    fun write(row: PlacedActionBlockRow) {
        clickableBlocksDao.create(row)
    }

    fun delete(row: PlacedActionBlockRow) {
        clickableBlocksDao.delete(row)
    }

    fun getAll(): List<PlacedActionBlockRow> {
        return clickableBlocksDao.queryForAll()
    }

}