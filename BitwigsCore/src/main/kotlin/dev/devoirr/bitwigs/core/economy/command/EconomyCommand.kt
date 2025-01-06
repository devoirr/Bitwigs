package dev.devoirr.bitwigs.core.economy.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.messages.Messages
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
            Messages.COMMAND_ECONOMY_CURRENCY_NOT_FOUND.getError().replace("{input}", currencyName)
                .sendTo(sender)
            return
        }

        if (amount < 0) {
            Messages.COMMAND_ECONOMY_AMOUNT_CANNOT_BE_NEGATIVE.getError().sendTo(sender)
            return
        }

        if (amount == 0.0 && type != EconomyCommandType.SET) {
            Messages.COMMAND_ECONOMY_AMOUNT_MUST_BE_POSITIVE.getError().sendTo(sender)
            return
        }

        if (!manager.databaseManager.isPlayerRegistered(uuid)) {
            Messages.COMMAND_ECONOMY_NOT_REGISTERED.getError().replace("{target}", targetName).sendTo(sender)
            return
        }

        when (type) {
            EconomyCommandType.ADD -> {
                manager.databaseManager.addMoney(uuid, currency, amount)
                Messages.COMMAND_ECONOMY_ADD_SUCCESS.getInfo().replace("{target}", targetName)
                    .replace("{amount}", amount.toString()).replace("{currency}", currency.symbol.toString())
                    .sendTo(sender)
            }

            EconomyCommandType.TAKE -> {
                val changes = manager.databaseManager.takeMoney(uuid, currency, amount)
                if (changes > 0) {
                    Messages.COMMAND_ECONOMY_TAKE_SUCCESS.getInfo().replace("{target}", targetName)
                        .replace("{amount}", amount.toString()).replace("{currency}", currency.symbol.toString())
                        .sendTo(sender)
                } else {
                    Messages.COMMAND_ECONOMY_NOT_ENOUGH_MONEY.getError().replace("{target}", targetName)
                        .sendTo(sender)
                }
            }

            EconomyCommandType.SET -> {
                manager.databaseManager.setMoney(uuid, currency, amount)
                Messages.COMMAND_ECONOMY_SET_SUCCESS.getInfo().replace("{target}", targetName)
                    .replace("{amount}", amount.toString()).replace("{currency}", currency.symbol.toString())
                    .sendTo(sender)
            }
        }
    }

}