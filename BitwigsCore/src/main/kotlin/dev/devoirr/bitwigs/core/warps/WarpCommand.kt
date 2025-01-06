package dev.devoirr.bitwigs.core.warps

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.messages.Messages
import dev.devoirr.bitwigs.core.warps.model.Warp
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WarpCommand(private val manager: WarpsManager) : BaseCommand() {

    @CommandAlias("setwarp|createwarp")
    @CommandPermission("bitwigs.warps.create")
    @Syntax("<Название>")
    @Description("Создаёт игровой варп")
    fun createWarp(player: Player, warpName: String) {
        if (!warpName.matches(manager.nameRegex)) {
            Messages.COMMAND_WARP_NAME_REGEX.getError().sendTo(player)
            return
        }

        if (manager.exists(warpName)) {
            Messages.COMMAND_WARP_ALREADY_EXISTS.getError().replace("{name}", warpName).sendTo(player)
            return
        }

        val warp = Warp(warpName, player.location, player.uniqueId)
        manager.createWarp(warp)

        Messages.COMMAND_WARP_CREATED.getInfo().replace("{name}", warpName).sendTo(player)
    }

    @CommandAlias("deletewarp|delwarp")
    @CommandPermission("bitwigs.warps.delete")
    @Syntax("<Название>")
    @Description("Удаляет игровой варп")
    fun deleteWarp(sender: CommandSender, name: String) {
        if (!manager.exists(name)) {
            Messages.COMMAND_WARP_NOT_FOUND.getError().replace("{name}", name).sendTo(sender)
            return
        }

        manager.deleteWarp(name)
        Messages.COMMAND_WARP_DELETED.getError().replace("{name}", name).sendTo(sender)
    }

    @CommandAlias("warp")
    @CommandPermission("bitwigs.warps.teleport")
    @Syntax("<Название> (Игрок)")
    @Description("Телепортирует игрока на варп")
    @CommandCompletion("@warps @visible")
    fun teleport(sender: CommandSender, warpName: String, @Optional targetName: String?) {

        if (targetName == null) {
            teleportSelf(sender, warpName)
            return
        }

        if (!sender.hasPermission("bitwigs.warps.teleport.others")) {
            teleportSelf(sender, warpName)
            return
        }

        if (sender.name == targetName) {
            teleportSelf(sender, warpName)
            return
        }

        if (!manager.exists(warpName)) {
            Messages.COMMAND_WARP_NOT_FOUND.getError().replace("{name}", warpName).sendTo(sender)
            return
        }

        val warp = manager.getWarp(warpName)
        val targetPlayer = Bukkit.getPlayerExact(targetName)

        if (targetPlayer == null) {
            Messages.PLAYER_NOT_FOUND.getError().replace("{name}", targetName).sendTo(sender)
            return
        }

        if (targetPlayer.hasPermission("bitwigs.unteleportable")) {
            Messages.PLAYER_UNTELEPORTABLE.getError().replace("{name}", targetName).sendTo(sender)
            return
        }

        targetPlayer.teleportAsync(warp.location)

        Messages.COMMAND_WARP_TELEPORTED_OTHER.getInfo().replace("{target}", targetName).replace("{name}", warpName)
            .sendTo(sender)
        Messages.COMMAND_WARP_TELEPORTED.getInfo().replace("{name}", warpName).sendTo(targetPlayer)

    }

    private fun teleportSelf(sender: CommandSender, warpName: String) {
        if (sender !is Player) {
            Messages.COMMAND_ONLY_FOR_PLAYERS.getError().sendTo(sender)
            return
        }

        if (!manager.exists(warpName)) {
            Messages.COMMAND_WARP_NOT_FOUND.getError().replace("{name}", warpName).sendTo(sender)
            return
        }

        val warp = manager.getWarp(warpName)
        sender.teleportAsync(warp.location)

        Messages.COMMAND_WARP_TELEPORTED.getInfo().replace("{name}", warpName).sendTo(sender)
    }

}