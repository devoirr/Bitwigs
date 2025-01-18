package dev.devoirr.bitwigs.core.warps

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.warps.model.Warp
import org.bukkit.entity.Player

class WarpsManager : Loadable {

    private val plugin = BitwigsPlugin.instance

    lateinit var databaseInfo: DatabaseInfo
    lateinit var database: WarpsDatabase

    private val loadedWarps = mutableMapOf<String, Warp>()

    lateinit var nameRegex: Regex

    override fun getName(): String {
        return "warps"
    }

    override fun onEnable() {
        databaseInfo =
            DatabaseInfo.parse(plugin.config.getConfigurationSection("warps.database")!!)
        database = WarpsDatabase(this)

        nameRegex = Regex(plugin.config.getString("warps.name-regex", "^[A-Za-z_]{3,10}\$")!!)

        plugin.commandManager.registerCommand(WarpCommand(this))
        plugin.commandManager.commandCompletions.registerCompletion("warps") {
            getAllWarpNames()
        }

        loadWarps()
    }

    override fun onDisable() {
    }

    private fun loadWarps() {
        database.getAllWarps().forEach {
            loadedWarps[it.name] = it
        }
    }

    fun exists(name: String): Boolean {
        return loadedWarps.containsKey(name)
    }

    fun createWarp(warp: Warp) {
        loadedWarps[warp.name] = warp
        database.createWarp(warp)
    }

    fun deleteWarp(name: String) {
        loadedWarps.remove(name)
        database.deleteWarp(name)
    }

    fun getWarp(name: String): Warp {
        return loadedWarps[name]!!
    }

    private fun getAllWarpNames() = loadedWarps.keys

    fun getPlayerWarps(player: Player) = loadedWarps.values.filter { it.creator == player.uniqueId }
}