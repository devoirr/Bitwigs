package dev.devoirr.bitwigs.core.decoration.furniture.sitting

import org.bukkit.configuration.serialization.ConfigurationSerializable

class Sitting(map: Map<String, Any>) : ConfigurationSerializable {

    val allowed = map["allowed"].toString().toBooleanStrict()
    val yOffset = map["chair-y-offset"].toString().toDouble()

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("allowed" to allowed, "chair-y-offset" to yOffset)
    }
}