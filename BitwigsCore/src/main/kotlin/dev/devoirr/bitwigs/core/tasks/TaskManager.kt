package dev.devoirr.bitwigs.core.tasks

import dev.devoirr.bitwigs.core.BitwigsPlugin
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitRunnable

class TaskManager(private val plugin: BitwigsPlugin) {

    fun runTaskLater(runnable: BukkitRunnable, time: Long) {
        runnable.runTaskLater(plugin, time)
    }

    fun runTaskLaterAsync(runnable: BukkitRunnable, time: Long) {
        runnable.runTaskLaterAsynchronously(plugin, time)
    }

    fun runTaskTime(runnable: BukkitRunnable, time: Long) {
        runnable.runTaskTimer(plugin, 0L, time)
    }

    fun runTaskLaterForEntity(entity: Entity, runnable: BukkitRunnable, time: Long) {
        entity.scheduler.runDelayed(plugin, {
            runnable.run()
        }, {}, time)
    }

    fun runTaskTimerForEntity(entity: Entity, runnable: BukkitRunnable, time: Long) {
        entity.scheduler.runAtFixedRate(plugin, {
            runnable.run()
        }, {}, 1L, time)
    }

}