package dev.devoirr.bitwigs.core.block.furniture.listener

import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable

class FurniturePlayerTimerListener(private val manager: FurnitureManager) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {

        manager.getTaskManager().runTaskTimerForEntity(event.player, object : BukkitRunnable() {
            override fun run() {
                manager.placedFurnitureHolder.sendChunkForPlayer(event.player, event.player.chunk)
            }
        }, 20 * 5)

    }

}