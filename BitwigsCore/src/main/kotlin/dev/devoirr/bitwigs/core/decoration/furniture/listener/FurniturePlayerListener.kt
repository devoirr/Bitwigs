package dev.devoirr.bitwigs.core.decoration.furniture.listener

import dev.devoirr.bitwigs.core.decoration.furniture.entities.FurnitureEntityManager
import dev.devoirr.bitwigs.core.listener.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerResourcePackStatusEvent

class FurniturePlayerListener : Listener() {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        FurnitureEntityManager.sendAllToPlayer(event.player)
    }

    @EventHandler
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        FurnitureEntityManager.sendAllToPlayer(event.player)
    }

    @EventHandler
    fun onResourcepackUpdate(event: PlayerResourcePackStatusEvent) {
        FurnitureEntityManager.sendAllToPlayer(event.player)
    }

}