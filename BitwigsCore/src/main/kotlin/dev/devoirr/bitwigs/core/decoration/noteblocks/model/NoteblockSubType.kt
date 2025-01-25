package dev.devoirr.bitwigs.core.decoration.noteblocks.model

import dev.devoirr.bitwigs.core.hasMetaAndModelData
import net.kyori.adventure.text.Component
import org.bukkit.Instrument
import org.bukkit.Material
import org.bukkit.Note
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.inventory.ItemStack

data class NoteblockSubType(
    val modelData: Int,
    val note: Int,
    val instrument: Instrument,
    val name: Component,
    val powered: Boolean
) {
    fun isThisType(itemStack: ItemStack): Boolean {
        if (itemStack.type != Material.SUGAR)
            return false
        if (!itemStack.hasMetaAndModelData())
            return false
        if (itemStack.itemMeta.customModelData != modelData)
            return false
        return true
    }

    fun isThisType(block: Block): Boolean {
        if (block.type != Material.NOTE_BLOCK)
            return false
        val blockData = block.blockData
        if (blockData !is NoteBlock)
            return false
        if (blockData.note.id != note.toByte()) {
            return false
        }
        if (blockData.instrument.name != instrument.name)
            return false
        if (blockData.isPowered != powered)
            return false
        return true
    }

    fun createBlockData(): BlockData {
        val data = Material.NOTE_BLOCK.createBlockData() as NoteBlock
        data.note = Note(note)
        data.instrument = instrument
        data.isPowered = powered
        return data
    }

    fun createItemStack(): ItemStack {
        val item = ItemStack(Material.SUGAR)
        val meta = item.itemMeta
        meta.setCustomModelData(modelData)
        meta.itemName(name)
        item.itemMeta = meta
        return item
    }
}