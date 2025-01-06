package dev.devoirr.bitwigs.core.warps

import com.google.common.cache.CacheBuilder
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.toLocation
import dev.devoirr.bitwigs.core.toString
import dev.devoirr.bitwigs.core.warps.model.Warp
import dev.devoirr.bitwigs.core.warps.model.WarpRow
import java.time.Duration
import java.util.*

class WarpsDatabase(manager: WarpsManager) {

    private val warpsDao: Dao<WarpRow, String>
    private val cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(60)).build<String, Warp>()

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, WarpRow::class.java)
        warpsDao = DaoManager.createDao(connectionSource, WarpRow::class.java)
    }

    fun getWarp(name: String): Warp? {
        cache.getIfPresent(name)?.let { w -> return w }

        if (!warpsDao.idExists(name))
            return null

        val row = warpsDao.queryForId(name)
        val warp = Warp(row.name, row.location.toLocation(), UUID.fromString(row.creator))

        return warp
    }

    fun exists(name: String): Boolean {
        return warpsDao.idExists(name)
    }

    fun createWarp(warp: Warp) {
        val row = WarpRow()

        row.name = warp.name
        row.creator = warp.creator.toString()
        row.location = warp.location.toString(false)

        warpsDao.create(row)
        cache.put(warp.name, warp)
    }

    fun deleteWarp(name: String) {
        warpsDao.deleteById(name)
        cache.invalidate(name)
    }

    fun getAllWarps(): List<Warp> {
        val list = mutableListOf<Warp>()
        warpsDao.forEach { warpRow ->
            list.add(Warp(warpRow.name, warpRow.location.toLocation(), UUID.fromString(warpRow.creator)))
        }
        return list
    }
}