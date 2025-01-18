package dev.devoirr.bitwigs.core.crates.model

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.randomizer.ItemWithChance
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class CratePrize(val key: String, private val chance: Double, private val commands: List<String>) :
    ItemWithChance {

    override fun getChance(): Double {
        return chance
    }

    fun executeFor(player: Player) {
        val playerCommands = mutableListOf<String>()
        val consoleCommands = mutableListOf<String>()

        for (command in commands) {

            if (command.startsWith("player: ")) {
                playerCommands.add(PlaceholderAPI.setPlaceholders(player, command.substring(8)))
            } else if (command.startsWith("console: ")) {
                consoleCommands.add(PlaceholderAPI.setPlaceholders(player, command.substring(9)))
            }

        }

        object : BukkitRunnable() {
            override fun run() {
                for (command in playerCommands) {
                    Bukkit.dispatchCommand(player, command)
                }

                for (command in consoleCommands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command)
                }
            }
        }.runTask(BitwigsPlugin.instance)
    }

}