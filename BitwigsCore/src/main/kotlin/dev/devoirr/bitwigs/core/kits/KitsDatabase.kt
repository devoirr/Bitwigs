package dev.devoirr.bitwigs.core.kits

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils

class KitsDatabase(manager: KitsManager) {

    private val kitsDao: Dao<Kit.KitRow, String>

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, Kit.KitRow::class.java)
        kitsDao = DaoManager.createDao(connectionSource, Kit.KitRow::class.java)
    }

    fun createKit(row: Kit.KitRow) {
        kitsDao.create(row)
    }

    fun getKit(name: String): Kit.KitRow? {
        if (!kitsDao.idExists(name))
            return null

        return kitsDao.queryForId(name)
    }

    fun deleteKit(name: String) {
        kitsDao.deleteById(name)
    }

    fun getAll(): List<Kit.KitRow> {
        return kitsDao.queryForAll()
    }

}