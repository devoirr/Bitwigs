package dev.devoirr.bitwigs.core.frames

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.BitwigsPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class FramesDatabase(manager: FramesManager) {

    private val framesDao: Dao<PacketFrameRow, String>

    private val removalQueue: Queue<Int> = LinkedList()
    private val creationQueue: Queue<PacketFrameRow> = LinkedList()

    init {
        val connectionSource = JdbcConnectionSource(manager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, PacketFrameRow::class.java)
        framesDao = DaoManager.createDao(connectionSource, PacketFrameRow::class.java)

        object : BukkitRunnable() {
            override fun run() {
                save()
            }
        }.runTaskTimerAsynchronously(BitwigsPlugin.instance, 0L, 20L * 60)
    }

    fun getAllFrames(): MutableList<PacketFrameRow> {
        return framesDao.queryForAll()
    }

    fun addFrame(row: PacketFrameRow) {
        removalQueue.removeAll { it == row.id }
        creationQueue.add(row)
    }

    fun removeFrame(id: Int) {
        creationQueue.removeAll { it.id == id }
        removalQueue.add(id)
    }

    private fun save() {

        var remove = removalQueue.first()
        while (remove != null) {
            framesDao.deleteById(remove.toString())
            remove = removalQueue.remove()
        }

        var save = creationQueue.remove()
        while (save != null) {
            framesDao.createOrUpdate(save)
            save = creationQueue.remove()
        }

    }
}