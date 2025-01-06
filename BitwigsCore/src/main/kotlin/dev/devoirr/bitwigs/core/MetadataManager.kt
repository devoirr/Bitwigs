package dev.devoirr.bitwigs.core

import org.bukkit.block.Block
import org.bukkit.metadata.FixedMetadataValue

class MetadataManager(private val plugin: BitwigsPlugin) {

    fun createMetadataValue(value: String): FixedMetadataValue {
        return FixedMetadataValue(plugin, value)
    }

    fun removeFromBlock(key: String, block: Block) {
        block.removeMetadata(key, plugin)
    }

}