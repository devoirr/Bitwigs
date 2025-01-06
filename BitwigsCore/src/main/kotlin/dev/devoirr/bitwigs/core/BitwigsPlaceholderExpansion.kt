package dev.devoirr.bitwigs.core

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class BitwigsPlaceholderExpansion(private val plugin: BitwigsPlugin) : PlaceholderExpansion() {

    override fun getIdentifier(): String {
        return "bitwigs"
    }

    override fun getAuthor(): String {
        return "devoirr"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun persist(): Boolean {
        return true
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {

        if (player == null)
            return null

        if (params == "balance") {
            val economyManager = plugin.economyManager ?: return ""

            return economyManager.databaseManager.readPlayerAccount(player.uniqueId)
                .getBalancesString(economyManager)
        } else if (params.startsWith("balance_")) {
            val economyManager = plugin.economyManager ?: return ""

            val currencyName = params.split("_")[1]
            val balance =
                economyManager.databaseManager.readPlayerAccount(player.uniqueId).getMoney(currencyName)

            if (params.endsWith("_pure")) {
                return balance.toString()
            }

            val currency = economyManager.getCurrency(currencyName) ?: return null
            return "$balance${currency.symbol}"
        }

        return super.onPlaceholderRequest(player, params)
    }
}