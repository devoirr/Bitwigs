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

            val economyService = BitwigsServices.economyService ?: return ""
            return economyService.getPlayerBalanceString(economyService.getPlayerAccount(player.uniqueId))

        } else if (params.startsWith("balance_")) {
            val economyService = BitwigsServices.economyService ?: return ""

            val currencyName = params.split("_")[1]
            val balance = economyService.getPlayerAccount(player.uniqueId).getMoney(currencyName)

            if (params.endsWith("_pure")) {
                return balance.toString()
            }

            val currency = economyService.getCurrency(currencyName) ?: return null
            return "$balance${currency.symbol}"
        }

        return super.onPlaceholderRequest(player, params)
    }
}