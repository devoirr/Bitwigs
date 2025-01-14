package dev.devoirr.bitwigs.core.blocks.clickable.model.database

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.blocks.clickable.ClickableBlocksManager

class ClickableBlocksDatabase(manager: ClickableBlocksManager) {

    private val clickableBlocksDao: Dao<PlacedClickableBlockRow, String>

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, PlacedClickableBlockRow::class.java)
        clickableBlocksDao = DaoManager.createDao(connectionSource, PlacedClickableBlockRow::class.java)
    }

    fun write(row: PlacedClickableBlockRow) {
        clickableBlocksDao.create(row)
    }

    fun delete(row: PlacedClickableBlockRow) {
        clickableBlocksDao.delete(row)
    }

    fun getAll(): List<PlacedClickableBlockRow> {
        return clickableBlocksDao.queryForAll()
    }

}