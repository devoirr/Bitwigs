package dev.devoirr.bitwigs.core.kits

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.database.DatabaseInfo
import dev.devoirr.bitwigs.core.module.Loadable
import dev.devoirr.bitwigs.core.toItemStack
import org.bukkit.inventory.EquipmentSlot

class KitsManager : Loadable {

    override fun getName(): String {
        return "kits"
    }

    private val plugin = BitwigsPlugin.instance

    lateinit var database: KitsDatabase
    lateinit var databaseInfo: DatabaseInfo

    private val kits = mutableMapOf<String, Kit>()

    override fun onEnable() {
        databaseInfo =
            DatabaseInfo.parse(plugin.config.getConfigurationSection("kits.database")!!)
        database = KitsDatabase(this)

        var kit: Kit
        database.getAll().forEach { row ->
            kit = Kit(row.name, row.cooldown, row.items.split(" ").map { it.toItemStack() }, mutableMapOf())

            row.helmet?.let { item -> kit.equipment[EquipmentSlot.HEAD] = item.toItemStack() }
            row.chestplate?.let { item -> kit.equipment[EquipmentSlot.CHEST] = item.toItemStack() }
            row.leggings?.let { item -> kit.equipment[EquipmentSlot.LEGS] = item.toItemStack() }
            row.boots?.let { item -> kit.equipment[EquipmentSlot.FEET] = item.toItemStack() }

            kits[row.name] = kit
        }

        plugin.commandManager.registerCommand(KitsCommand(this))

    }

    fun getKit(name: String) = kits[name]
    fun saveKit(kit: Kit) {
        kits[kit.name] = kit
        database.createKit(kit.convertToRow())
    }

    override fun onDisable() {

    }
}