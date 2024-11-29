package dev.devoirr.bitwigs.core

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class BitwigsPlugin: JavaPlugin() {

    override fun onEnable() {
        Bukkit.getLogger().info("Enabled Bitwigs!")
    }

}