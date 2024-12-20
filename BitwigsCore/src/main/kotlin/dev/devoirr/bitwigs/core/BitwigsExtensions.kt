package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.util.ComponentUtility
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

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