package dev.devoirr.bitwigs.core.blocks.action.model

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.BlockEffect
import dev.devoirr.bitwigs.core.blocks.ReplacerInfo
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.cooldown.Cooldown
import dev.devoirr.bitwigs.core.getStringOrNull
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class ActionBlockType(
    val permission: String?,
    val permissionMessage: String?,
    val commands: List<String>,
    val effect: BlockEffect?,
    val perPlayerCooldown: Cooldown?,
    val globalCooldown: Cooldown?,
    val globalCooldownReplacer: ReplacerInfo?
) {

    companion object {

        fun parse(section: ConfigurationSection): ActionBlockType {

            val permission = section.getStringOrNull("permission")
            val permissionMessage = section.getStringOrNull("permission-message")

            val effect = if (section.getKeys(false).contains("effect"))
                BlockEffect.parse(section.getConfigurationSection("effect")!!) else null

            val commands = section.getStringList("commands")
            val perPlayerCooldown = if (section.getKeys(false).contains("per-player-cooldown"))
                Cooldown.parse(section.getConfigurationSection("per-player-cooldown")!!) else null

            val globalCooldown: Cooldown?
            val globalCooldownReplacer: ReplacerInfo?
            if (section.getKeys(false).contains("global-cooldown")) {
                globalCooldown = Cooldown.parse(section.getConfigurationSection("global-cooldown")!!)
                globalCooldownReplacer =
                    ReplacerInfo.createFromSection(section.getConfigurationSection("global-cooldown.replace")!!)
            } else {
                globalCooldown = null
                globalCooldownReplacer = null
            }

            return ActionBlockType(
                permission,
                permissionMessage,
                commands,
                effect,
                perPlayerCooldown,
                globalCooldown,
                globalCooldownReplacer
            )

        }

    }

    fun runFor(player: Player, block: Block) {

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

                effect?.playAt(block.location.centralize())
            }
        }.runTask(BitwigsPlugin.instance)

    }

    fun hasPerPlayerCooldown() = perPlayerCooldown != null
    fun hasGlobalCooldown() = globalCooldown != null

}