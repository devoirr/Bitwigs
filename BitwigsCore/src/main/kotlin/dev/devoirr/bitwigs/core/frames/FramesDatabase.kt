package dev.devoirr.bitwigs.core.frames

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils

class FramesDatabase(manager: FramesManager) {

    private val framesDao: Dao<PacketFrameRow, String>

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, PacketFrameRow::class.java)
        framesDao = DaoManager.createDao(connectionSource, PacketFrameRow::class.java)
    }


}