package dev.devoirr.bitwigs.core.economy.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.locale.Locale
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("economy|eco")
@CommandPermission("bitwigs.command.economy")
@Description("Управление экономикой сервера")
class EconomyCommand(private val manager: EconomyManager) : BaseCommand() {

    enum class EconomyCommandType {
        SET,
        ADD,
        TAKE
    }

    @Subcommand("add")
    @CommandCompletion("@players @currencies")
    @Syntax("<Игрок> <Валюта> <Сумма>")
    @Description("Добавляет деньги на счёт игроку")
    fun executeAdd(sender: CommandSender, targetName: String, currencyName: String, amount: Double) {
        handleTransaction(sender, targetName, currencyName, amount, EconomyCommandType.ADD)
    }

    @Subcommand("take")
    @CommandCompletion("@players @currencies")
    @Syntax("(игрок) (валюта) (сумма)")
    @Description("Забирает деньги со счёта игрока")
    fun executeTake(sender: CommandSender, targetName: String, currencyName: String, amount: Double) {
        handleTransaction(sender, targetName, currencyName, amount, EconomyCommandType.TAKE)
    }

    @Subcommand("set")
    @CommandCompletion("@players @currencies")
    @Syntax("(игрок) (валюта) (сумма)")
    @Description("Устанавливает деньги на счёт игроку")
    fun executeSet(sender: CommandSender, targetName: String, currencyName: String, amount: Double) {
        handleTransaction(sender, targetName, currencyName, amount, EconomyCommandType.SET)
    }

    private fun handleTransaction(
        sender: CommandSender,
        targetName: String,
        currencyName: String,
        amount: Double,
        type: EconomyCommandType
    ) {

        val target = Bukkit.getOfflinePlayer(targetName)
        val uuid = target.uniqueId

        val currency = manager.getCurrency(currencyName)
        if (currency == null) {
            Locale.currencyNotFound.send(sender, "{input}" to currencyName)
            return
        }

        if (amount < 0) {
            Locale.transactionCantBeNegative.send(sender)
            return
        }

        if (amount == 0.0 && type != EconomyCommandType.SET) {
            Locale.transactionMustBePositive.send(sender)
            return
        }

        if (!manager.databaseManager.isPlayerRegistered(uuid)) {
            Locale.accountNotRegistered.send(sender, "{target}" to targetName)
            return
        }

        when (type) {
            EconomyCommandType.ADD -> {
                manager.databaseManager.addMoney(uuid, currency, amount)
                Locale.transactionAddSuccess.send(
                    sender,
                    "{target}" to targetName,
                    "{currency}" to currency.symbol.toString(),
                    "{amount}" to amount.toString()
                )
            }

            EconomyCommandType.TAKE -> {
                val changes = manager.databaseManager.takeMoney(uuid, currency, amount)
                if (changes > 0) {
                    Locale.transactionTakeSuccess.send(
                        sender,
                        "{target}" to targetName,
                        "{currency}" to currency.symbol.toString(),
                        "{amount}" to amount.toString()
                    )
                } else {
                    Locale.transactionTakeNotEnough.send(sender)
                }
            }

            EconomyCommandType.SET -> {
                manager.databaseManager.setMoney(uuid, currency, amount)
                Locale.transactionSetSuccess.send(
                    sender,
                    "{target}" to targetName,
                    "{currency}" to currency.symbol.toString(),
                    "{amount}" to amount.toString()
                )
            }
        }
    }

}