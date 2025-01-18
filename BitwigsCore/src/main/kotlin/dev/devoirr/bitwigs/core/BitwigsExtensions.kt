package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.cooldown.Cooldown
import dev.devoirr.bitwigs.core.cooldown.CooldownManager
import dev.devoirr.bitwigs.core.decoration.model.Tool
import dev.devoirr.bitwigs.core.util.TextUtility
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun String.toComponent() = LegacyComponentSerializer.legacyAmpersand().deserialize(this)
    .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)

fun List<String>.toComponent() = TextUtility.listOfStringToComponent(this)

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

fun Location.centralize(): Location {
    return this.clone().add(0.5, 0.5, 0.5)
}

fun Player.hasCooldownBypass() = this.hasPermission("bitwigs.cooldowns.bypass")
fun Player.getGroup(): String {
    return BitwigsPlugin.instance.permission?.getPrimaryGroup(this) ?: "default"
}

fun Player.getLeft(cooldown: Cooldown): String? = CooldownManager.getLeft(this, cooldown)

fun Player.addCooldown(cooldown: Cooldown) {
    CooldownManager.addToPlayer(this, cooldown)
}

fun ItemStack.getTool(): Tool? {
    return Tool.entries.firstOrNull { it.isThisTool(this) }
}

fun Player.getWarpsLimit(): Int {
    var limit = 0
    for (permission in this.effectivePermissions) {
        if (permission.permission.startsWith("bitwigs.warps.create.")) {
            limit = permission.permission.substring(21).toInt().coerceAtLeast(limit)
        }
    }
    return limit
}

fun Player.getHomesLimit(): Int {
    var limit = 0
    for (permission in this.effectivePermissions) {
        if (permission.permission.startsWith("bitwigs.homes.create.")) {
            limit = permission.permission.substring(21).toInt().coerceAtLeast(limit)
        }
    }
    return limit
}

fun Player.isUnteleportable() = this.hasPermission("bitwigs.unteleportable")

fun Player.hasUnlimitedHomes() = this.hasPermission("bitwigs.homes.create.unlimited")
fun Player.hasUnlimitedWarps() = this.hasPermission("bitwigs.warps.create.unlimited")

fun Player.canDeleteOthersWarps() = this.hasPermission("bitwigs.warps.delete.others")