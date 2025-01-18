package dev.devoirr.bitwigs.core.kits

import com.j256.ormlite.field.DataType
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import dev.devoirr.bitwigs.core.gui.Menu
import dev.devoirr.bitwigs.core.toBase64
import dev.devoirr.bitwigs.core.toComponent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

data class Kit(
    val name: String,
    val cooldown: Int,
    val items: List<ItemStack>,
    val equipment: MutableMap<EquipmentSlot, ItemStack>
) {

    @DatabaseTable(tableName = "kits")
    class KitRow {
        @DatabaseField(canBeNull = false, id = true)
        lateinit var name: String

        @DatabaseField(canBeNull = false, dataType = DataType.INTEGER)
        var cooldown: Int = 0

        @DatabaseField(canBeNull = false)
        lateinit var items: String

        @DatabaseField(canBeNull = true)
        var helmet: String? = null

        @DatabaseField(canBeNull = true)
        var chestplate: String? = null

        @DatabaseField(canBeNull = true)
        var leggings: String? = null

        @DatabaseField(canBeNull = true)
        var boots: String? = null
    }

    enum class KitSlot(private val equipmentSlot: EquipmentSlot) {
        HELMET(EquipmentSlot.HEAD),
        CHEST(EquipmentSlot.CHEST),
        LEGS(EquipmentSlot.LEGS),
        FEET(EquipmentSlot.FEET);

        fun get(kit: Kit): ItemStack? {
            return kit.equipment[this.equipmentSlot]
        }
    }

    fun convertToRow(): KitRow {
        val row = KitRow()

        row.name = name
        row.cooldown = cooldown

        row.helmet = KitSlot.HELMET.get(this)?.toBase64()
        row.chestplate = KitSlot.CHEST.get(this)?.toBase64()
        row.leggings = KitSlot.LEGS.get(this)?.toBase64()
        row.boots = KitSlot.FEET.get(this)?.toBase64()

        val base64 = items.joinToString(separator = " ") { it.toBase64() }
        row.items = base64

        return row
    }

    fun give(player: Player) {
        val itemsToGive = items.map { it.clone() }.toMutableList()
        while (player.inventory.firstEmpty() != -1 && itemsToGive.isNotEmpty()) {
            player.inventory.addItem(itemsToGive.removeFirst())
        }

        while (itemsToGive.isNotEmpty()) {
            player.world.dropItemNaturally(player.location, itemsToGive.removeFirst())
        }

        for (slot in equipment.keys) {

            if (player.inventory.getItem(slot).type == Material.AIR) {
                player.inventory.setItem(slot, equipment[slot]!!)
            } else if (player.inventory.firstEmpty() != -1) {
                player.inventory.addItem(equipment[slot]!!)
            } else {
                player.world.dropItemNaturally(player.location, equipment[slot]!!)
            }

        }
    }

    fun getPreview(): Menu {

        var menu = Menu().title("Набор $name".toComponent()).size(54)

        KitSlot.HELMET.get(this)?.let { menu = menu.item({ _ -> it }, { _, _ -> }, 2) }
        KitSlot.CHEST.get(this)?.let { menu = menu.item({ _ -> it }, { _, _ -> }, 3) }

        KitSlot.LEGS.get(this)?.let { menu = menu.item({ _ -> it }, { _, _ -> }, 5) }
        KitSlot.FEET.get(this)?.let { menu = menu.item({ _ -> it }, { _, _ -> }, 6) }

        items.forEach { item ->
            menu = menu.addItem({ _ -> item }, { _, _ -> }, 18)
        }

        return menu

    }

}
