package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockSubType
import dev.devoirr.bitwigs.core.block.noteblocks.model.type.NoteblockType
import dev.devoirr.bitwigs.core.util.ComponentUtility
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun String.toComponent() = LegacyComponentSerializer.legacyAmpersand().deserialize(this)
fun List<String>.toComponent() = ComponentUtility.listOfStringToComponent(this)

fun Location.toString(block: Boolean): String {
    val stringBuilder = StringBuilder()
        .append(this.world!!.name).append(";")
        .append(if (block) this.blockX else this.x)
        .append(";")
        .append(if (block) this.blockY else this.z)
        .append(";")
        .append(if (block) this.blockZ else this.z)

    if (!block) {
        stringBuilder.append(";")
            .append(this.yaw)
            .append(";")
            .append(this.pitch)
    }

    return stringBuilder.toString()
}

fun String.toLocation(): Location {
    val args = this.split(";")
    val world = Bukkit.getWorld(args[0])

    val x = args[1].toDouble()
    val y = args[2].toDouble()
    val z = args[3].toDouble()

    val yaw = if (args.size > 4) args[4].toFloat() else 0f
    val pitch = if (args.size > 5) args[5].toFloat() else 0f

    return Location(world, x, y, z, yaw, pitch)
}

fun String.toIntegerList(): List<Int> {
    val list = mutableListOf<Int>()
    val args = this.replace(" ", "").split(",")

    for (item in args) {
        if (item.contains("-")) {
            val firstInRange = item.split("-")[0].toInt()
            val secondInRange = item.split("-")[1].toInt()

            for (i in firstInRange..secondInRange) {
                list.add(i)
            }
        } else {
            list.add(Integer.valueOf(item))
        }
    }

    return list
}

fun Double.twoDeciminalPlaces(): Double {
    return String.format("%.2f", this).toDouble()
}

fun ItemStack.isSameModelData(secondItem: ItemStack): Boolean {
    if (this.hasItemMeta()) {
        if (!secondItem.hasItemMeta())
            return false

        if (this.itemMeta.hasCustomModelData()) {
            if (!secondItem.itemMeta.hasCustomModelData())
                return false
        }

        return this.itemMeta.customModelData == secondItem.itemMeta.customModelData
    }

    return !secondItem.hasItemMeta()
}

fun BlockFace.getRight(): BlockFace {
    return when (this) {
        BlockFace.NORTH -> BlockFace.EAST
        BlockFace.EAST -> BlockFace.SOUTH
        BlockFace.SOUTH -> BlockFace.WEST
        BlockFace.WEST -> BlockFace.NORTH
        else -> BlockFace.NORTH
    }
}

fun BlockFace.getLeft(): BlockFace {
    return when (this) {
        BlockFace.NORTH -> BlockFace.WEST
        BlockFace.WEST -> BlockFace.SOUTH
        BlockFace.SOUTH -> BlockFace.EAST
        BlockFace.EAST -> BlockFace.NORTH
        else -> BlockFace.NORTH
    }
}

fun ItemStack.withAmount(amount: Int): ItemStack {
    this.amount = amount
    return this
}

fun ItemStack.withModelData(modelData: Int): ItemStack {
    val meta = itemMeta
    meta.setCustomModelData(modelData)
    this.setItemMeta(meta)
    return this
}

fun ItemStack.toBase64(): String {
    val io = ByteArrayOutputStream()
    val os = BukkitObjectOutputStream(io)

    os.writeObject(this)
    os.flush()

    val serializedObject = io.toByteArray()
    return java.util.Base64.getEncoder().encodeToString(serializedObject)
}

fun ItemStack.withInstrument(instrument: String) {
    val meta = itemMeta
    val dataContainer = meta.persistentDataContainer

    dataContainer.set(NamespacedKey.minecraft("instrument"), PersistentDataType.STRING, instrument)
    itemMeta = meta
}

fun ItemStack.withNote(note: Int) {
    val meta = itemMeta
    val dataContainer = meta.persistentDataContainer

    dataContainer.set(NamespacedKey.minecraft("note"), PersistentDataType.INTEGER, note)
    itemMeta = meta
}

fun ItemStack.withPowered(powered: Boolean) {
    val meta = itemMeta
    val dataContainer = meta.persistentDataContainer

    dataContainer.set(NamespacedKey.minecraft("powered"), PersistentDataType.BOOLEAN, powered)
    itemMeta = meta
}

fun String.toItemStack(): ItemStack {
    val serializedObject = java.util.Base64.getDecoder().decode(this)

    val input = ByteArrayInputStream(serializedObject)
    val inputStream = BukkitObjectInputStream(input)

    return inputStream.readObject() as ItemStack
}

fun ItemStack.isTool(): Boolean {
    return this.type == Material.SHEARS
            || this.type.name.endsWith("SHOVEL")
            || this.type.name.endsWith("SWORD")
            || this.type.name.endsWith("AXE")
            || this.type.name.endsWith("_HOE")
}

fun ConfigurationSection.getStringOrNull(key: String): String? {
    if (getKeys(false).contains(key)) {
        return getString(key)!!
    }

    return null
}

fun ItemStack.hasMetaAndModelData(): Boolean {
    return this.hasItemMeta() && this.hasCustomModelData()
}

fun Pair<NoteblockType, NoteblockSubType>.type(): NoteblockType {
    return this.first
}

fun Pair<NoteblockType, NoteblockSubType>.subType(): NoteblockSubType {
    return this.second
}