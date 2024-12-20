package dev.devoirr.bitwigs.core.furniture.model

import org.bukkit.Material

class PerItemParameter(private val default: String) {

    private val map = mutableMapOf<Material, String>()

    fun add(material: Material, value: String) {
        map[material] = value
    }

    fun get(material: Material) = map.getOrDefault(material, default)

}