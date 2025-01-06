package dev.devoirr.bitwigs.core.warps

import dev.devoirr.bitwigs.core.BitwigsFactory
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.warps.model.Warp

class WarpsManager(private val plugin: BitwigsPlugin) {

    var isEnabled = false
        private set

    lateinit var databaseInfo: DatabaseInfo
    lateinit var database: WarpsDatabase

    private val loadedWarps = mutableMapOf<String, Warp>()

    lateinit var nameRegex: Regex

    fun onEnable() {
        isEnabled = true

        databaseInfo =
            BitwigsFactory.databaseInfoFactory.parse(plugin.config.getConfigurationSection("warps.database")!!)
        database = WarpsDatabase(this)

        nameRegex = Regex(plugin.config.getString("warps.name-regex", "^[A-Za-z_]{3,10}\$")!!)

        plugin.commandManager.registerCommand(WarpCommand(this))

        loadWarps()
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

    fun getAllWarpNames() = loadedWarps.keys
}