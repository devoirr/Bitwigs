package dev.devoirr.bitwigs.core.economy

import dev.devoirr.bitwigs.core.economy.model.Account
import dev.devoirr.bitwigs.core.economy.model.Currency
import java.util.*

interface EconomyService {

    fun getPlayerAccount(uuid: UUID): Account
    fun getPlayerBalanceString(account: Account): String
    fun getCurrency(string: String): Currency?

}