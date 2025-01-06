package dev.devoirr.bitwigs.core.economy.model.database

import com.google.common.cache.CacheBuilder
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.economy.model.Account
import dev.devoirr.bitwigs.core.economy.model.Currency
import java.time.Duration
import java.util.*

class EconomyDatabaseManager(private val economyManager: EconomyManager) {

    private val economyDao: Dao<AccountRow, String>
    private val cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(60)).build<String, Account>()

    init {
        val connectionSource = JdbcConnectionSource(economyManager.databaseInfo.connectionString)
        TableUtils.createTableIfNotExists(connectionSource, AccountRow::class.java)
        economyDao = DaoManager.createDao(connectionSource, AccountRow::class.java)
    }

    fun addMoney(uuid: UUID, currency: Currency, amount: Double): Int {
        cache.invalidate(uuid.toString())
        val server = getCurrencyServer(currency)

        val updateBuilder = economyDao.updateBuilder()
        updateBuilder.where().eq("uuid", uuid.toString()).and().eq("server", server).and()
            .eq("currency", currency.key)
        updateBuilder.updateColumnExpression("balance", "balance+$amount")

        val updatedLines = updateBuilder.update()
        return updatedLines
    }

    fun takeMoney(uuid: UUID, currency: Currency, amount: Double): Int {
        cache.invalidate(uuid.toString())
        val server = getCurrencyServer(currency)

        val updateBuilder = economyDao.updateBuilder()
        updateBuilder.where().eq("uuid", uuid.toString()).and().eq("server", server).and()
            .eq("currency", currency.key).and().ge("balance", amount)

        updateBuilder.updateColumnExpression("balance", "balance-$amount")

        val updatedLines = updateBuilder.update()
        return updatedLines

    }

    fun setMoney(uuid: UUID, currency: Currency, amount: Double): Int {
        cache.invalidate(uuid.toString())
        val server = getCurrencyServer(currency)

        val updateBuilder = economyDao.updateBuilder()
        updateBuilder.where().eq("uuid", uuid.toString()).and().eq("server", server).and()
            .eq("currency", currency.key).and().ge("balance", amount)

        updateBuilder.updateColumnValue("balance", amount)

        val updatedLines = updateBuilder.update()
        return updatedLines
    }

    private fun getCurrencyServer(currency: Currency): String {
        val server = if (currency.global) "global" else economyManager.getServerId()
        return server
    }

    private fun writePlayerAccount(account: Account) {
        val rows = account.toDaoRows(economyManager)
        for (row in rows) {
            economyDao.create(row)
        }
    }

    fun writeAccountIfNotExists(account: Account) {
        val existingRows = economyDao.queryForFieldValuesArgs(mapOf("uuid" to account.uuid.toString()))
        if (existingRows.isNotEmpty())
            return

        writePlayerAccount(account)
    }

    fun writeAccountCurrencyIfNotExists(uuid: UUID, currency: Currency) {
        val rows = economyDao.queryForFieldValuesArgs(mapOf("uuid" to uuid.toString(), "currency" to currency))
        if (rows.isNotEmpty())
            return

        val row = AccountRow()

        row.uuid = uuid.toString()
        row.balance = currency.newbie
        row.server = getCurrencyServer(currency)
        row.currency = currency.key

        economyDao.create(row)
    }

    fun readPlayerAccount(uuid: UUID): Account {

        cache.getIfPresent(uuid.toString())?.let {
            return it
        }

        val account = Account(uuid)

        val globalRows = economyDao.queryForFieldValuesArgs(mapOf("uuid" to uuid.toString(), "server" to "global"))
        for (row in globalRows) {
            account.setMoney(row.currency, row.balance)
        }

        val localRows = economyDao.queryForFieldValuesArgs(
            mapOf(
                "uuid" to uuid.toString(),
                "server" to economyManager.getServerId()
            )
        )
        for (row in localRows) {
            account.setMoney(row.currency, row.balance)
        }

        cache.put(uuid.toString(), account)
        return account

    }

    fun isPlayerRegistered(uuid: UUID): Boolean {
        if (cache.asMap().containsKey(uuid.toString()))
            return true

        val rows = economyDao.queryForFieldValuesArgs(mapOf("uuid" to uuid.toString()))
        println(rows.count())
        return rows.isNotEmpty()
    }

}