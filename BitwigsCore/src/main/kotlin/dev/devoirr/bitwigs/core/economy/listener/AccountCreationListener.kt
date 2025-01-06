package dev.devoirr.bitwigs.core.economy.listener

import dev.devoirr.bitwigs.core.economy.EconomyManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class AccountCreationListener(private val manager: EconomyManager) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        manager.createAccount(event.player)
    }

}