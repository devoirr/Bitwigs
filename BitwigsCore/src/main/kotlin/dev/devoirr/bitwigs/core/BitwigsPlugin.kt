package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.test.TestCommand
import org.bukkit.plugin.java.JavaPlugin

class BitwigsPlugin : JavaPlugin() {

    override fun onEnable() {
        print("Enabled Bitwigs!")

        server.commandMap.register("test", TestCommand())
    }

}