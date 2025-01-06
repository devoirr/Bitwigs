package dev.devoirr.bitwigs.core.economy

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.economy.command.BalanceCommand
import dev.devoirr.bitwigs.core.economy.command.EconomyCommand
import dev.devoirr.bitwigs.core.economy.listener.AccountCreationListener
import dev.devoirr.bitwigs.core.economy.model.Currency
import dev.devoirr.bitwigs.core.economy.model.database.EconomyDatabaseManager
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.scheduler.BukkitRunnable
import java.io.File

class EconomyManager(private val plugin: BitwigsPlugin) {

    lateinit var databaseManager: EconomyDatabaseManager
    lateinit var databaseInfo: DatabaseInfo

    private val config = Config(File(plugin.dataFolder, "economy.yml"))

    private val loadedCurrencies = mutableMapOf<String, Currency>()

    private val creationListener = AccountCreationListener(this)

    var isEnabled = false
        private set

    fun onEnable() {

        databaseInfo =
            BitwigsFactory.databaseInfoFactory.parse(plugin.config.getConfigurationSection("economy.database")!!)
        databaseManager = EconomyDatabaseManager(this)

        config.get().getConfigurationSection("currencies")?.getKeys(false)?.forEach {
            loadedCurrencies[it] =
                BitwigsFactory.currencyFactory.parse(config.get().getConfigurationSection("currencies.$it")!!)
        }

        plugin.commandManager.registerCommand(BalanceCommand(this))
        plugin.commandManager.registerCommand(EconomyCommand(this))

        plugin.server.pluginManager.registerEvents(creationListener, plugin)

        isEnabled = true
    }

    fun onDisable() {
        HandlerList.unregisterAll(creationListener)
        isEnabled = false
    }

    fun createAccount(player: Player) {
        object : BukkitRunnable() {
            override fun run() {
                loadedCurrencies.values.forEach { currency ->
                    databaseManager.writeAccountCurrencyIfNotExists(player.uniqueId, currency)
                }
            }
        }.runTaskAsynchronously(plugin)
    }

    fun getAllCurrencyKeys() = loadedCurrencies.keys
    fun getServerId() = plugin.uniqueServerId
    fun getCurrency(name: String) = loadedCurrencies[name]

}