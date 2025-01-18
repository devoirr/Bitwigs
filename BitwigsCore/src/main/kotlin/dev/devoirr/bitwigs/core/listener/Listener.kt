package dev.devoirr.bitwigs.core.listener

import dev.devoirr.bitwigs.core.BitwigsPlugin
import org.bukkit.Bukkit
import org.bukkit.event.Listener

open class Listener : Listener {

    fun register() {
        Bukkit.getPluginManager().registerEvents(this, BitwigsPlugin.instance)
    }
    
}