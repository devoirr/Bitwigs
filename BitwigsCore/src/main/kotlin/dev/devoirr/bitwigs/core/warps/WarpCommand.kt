package dev.devoirr.bitwigs.core.warps

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.canDeleteOthersWarps
import dev.devoirr.bitwigs.core.getWarpsLimit
import dev.devoirr.bitwigs.core.hasUnlimitedWarps
import dev.devoirr.bitwigs.core.isUnteleportable
import dev.devoirr.bitwigs.core.locale.Locale
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

        if (!player.hasUnlimitedWarps()) {
            val maxWarps = player.getWarpsLimit()
            if (maxWarps >= manager.getPlayerWarps(player).size) {
                Locale.warpsLimit.send(player)
                return
            }
        }

        if (manager.exists(warpName)) {
            Locale.warpAlreadyExists.send(player, "{name}" to warpName)
            return
        }

        if (!warpName.matches(manager.nameRegex)) {
            Locale.warpNameRegex.send(player)
            return
        }

        val warp = Warp(warpName, player.location, player.uniqueId)
        manager.createWarp(warp)

        Locale.warpCreated.send(player, "{name}" to warpName)
    }

    @CommandAlias("deletewarp|delwarp")
    @CommandPermission("bitwigs.warps.delete")
    @Syntax("<Название>")
    @Description("Удаляет игровой варп")
    fun deleteWarp(sender: CommandSender, name: String) {
        if (!manager.exists(name)) {
            Locale.warpNotFound.send(sender, "{name}" to name)
            return
        }

        val warp = manager.getWarp(name)
        if (sender is Player) {
            if (sender.uniqueId != warp.creator && !sender.canDeleteOthersWarps()) {
                Locale.warpsDeleteOnlyOwn.send(sender)
                return
            }
        }

        manager.deleteWarp(name)
        Locale.warpDeleted.send(sender, "{name}" to name)
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
            Locale.warpNotFound.send(sender, "{name}" to name)
            return
        }

        val warp = manager.getWarp(warpName)
        val targetPlayer = Bukkit.getPlayerExact(targetName)

        if (targetPlayer == null) {
            Locale.playerNotFound.send(sender, "{name}" to targetName)
            return
        }

        if (targetPlayer.isUnteleportable()) {
            Locale.playerUnteleportable.send(sender, "{name}" to targetName)
            return
        }

        targetPlayer.teleportAsync(warp.location)

        Locale.warpTeleportedOther.send(sender, "{target}" to targetName, "{name}" to warpName)
        Locale.warpTeleported.send(targetPlayer, "{name}" to warpName)
    }

    private fun teleportSelf(sender: CommandSender, warpName: String) {
        if (sender !is Player) {
            Locale.commandForPlayers.send(sender)
            return
        }

        if (!manager.exists(warpName)) {
            Locale.warpNotFound.send(sender, "{name}" to name)
            return
        }

        val warp = manager.getWarp(warpName)
        sender.teleportAsync(warp.location)

        Locale.warpTeleported.send(sender, "{name}" to warpName)
    }

}