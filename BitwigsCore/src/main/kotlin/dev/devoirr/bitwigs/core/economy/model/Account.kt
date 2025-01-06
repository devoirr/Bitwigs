package dev.devoirr.bitwigs.core.economy.model

import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.economy.model.database.AccountRow
import java.util.*

class Account(val uuid: UUID) {

    private val balances = mutableMapOf<String, Double>()

    private var balanceString: String? = null
    private var balanceStringUpdated: Long = 0L

    fun setMoney(currency: String, money: Double) {
        balances[currency] = money
    }

    fun getMoney(currency: String) = balances.getOrDefault(currency, 0.0)

    fun getBalancesString(manager: EconomyManager): String {
        balanceString?.let { string ->
            if (System.currentTimeMillis() - balanceStringUpdated > 5000L)
                return string
        }
        
        var stringBuilder = StringBuilder()

        balances.keys.forEachIndexed { index, currencyName ->
            manager.getCurrency(currencyName)?.let { currency ->
                if (index != 0)
                    stringBuilder = stringBuilder.append(" ")

                stringBuilder = stringBuilder.append(balances[currencyName]!!).append(currency.symbol)
            }
        }

        balanceStringUpdated = System.currentTimeMillis()
        balanceString = stringBuilder.toString()

        return balanceString!!
    }

    fun toDaoRows(manager: EconomyManager): List<AccountRow> {

        val list = mutableListOf<AccountRow>()

        var row: AccountRow
        balances.forEach { (currency, amount) ->
            row = AccountRow()

            row.uuid = uuid.toString()
            row.currency = currency
            row.balance = amount
            row.server = if (manager.getCurrency(currency)!!.global) "global" else manager.getServerId()

            list.add(row)
        }

        return list

    }

}