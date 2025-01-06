package dev.devoirr.bitwigs.core.block.furniture.listener

import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent

class FurniturePlayerListener(private val manager: FurnitureManager) : Listener {

    @EventHandler
    fun onWorldJoin(event: PlayerChangedWorldEvent) {
        manager.placedFurnitureHolder.sendAllForPlayer(event.player)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        manager.placedFurnitureHolder.sendAllForPlayer(event.player)
    }

}