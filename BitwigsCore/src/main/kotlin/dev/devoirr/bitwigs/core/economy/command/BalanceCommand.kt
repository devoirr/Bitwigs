package dev.devoirr.bitwigs.core.economy.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Optional
import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.locale.Locale
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("balance|bal")
class BalanceCommand(private val manager: EconomyManager) : BaseCommand() {

    @Default
    fun execute(sender: CommandSender, @Optional targetName: String?) {
        if (targetName == null)
            handleForOwnBalance(sender)
        else
            handleForOthersBalance(sender, targetName)
    }

    private fun handleForOwnBalance(sender: CommandSender) {
        if (sender !is Player) {
            Locale.commandForPlayers.send(sender)
            return
        }

        val account = manager.databaseManager.readPlayerAccount(sender.uniqueId)
        var balance = account.getBalancesString(manager)

        if (balance.isBlank()) {
            balance = Locale.emptyBalance.literal
        }

        Locale.balanceResultOwn.send(sender, "{balance}" to balance)
        return
    }

    private fun handleForOthersBalance(sender: CommandSender, targetName: String) {
        if (targetName == sender.name) {
            handleForOwnBalance(sender)
            return
        }

        if (!sender.hasPermission("bitwigs.economy.balance.others")) {
            Locale.balanceOnlyOwn.send(sender)
            return
        }

        val targetId = Bukkit.getOfflinePlayer(targetName).uniqueId
        val account = manager.databaseManager.readPlayerAccount(targetId)
        var balance = account.getBalancesString(manager)

        if (balance.isBlank()) {
            balance = Locale.emptyBalance.literal
        }


        Locale.balanceResultOther.send(sender, "{balance}" to balance)
    }

}