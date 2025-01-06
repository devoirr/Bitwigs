package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.block.furniture.model.fabric.BlockEffectFactory
import dev.devoirr.bitwigs.core.block.furniture.model.fabric.FurnitureItemComparatorFactory
import dev.devoirr.bitwigs.core.block.furniture.model.fabric.HitboxFactory
import dev.devoirr.bitwigs.core.block.furniture.model.fabric.PerItemParameterFactory
import dev.devoirr.bitwigs.core.block.noteblocks.model.factory.NoteblockTypeFactory
import dev.devoirr.bitwigs.core.chat.model.factory.ChatChannelFactory
import dev.devoirr.bitwigs.core.database.DatabaseInfoFactory
import dev.devoirr.bitwigs.core.economy.model.factory.CurrencyFactory

class BitwigsFactory {

    companion object {
        val databaseInfoFactory = DatabaseInfoFactory()
        val blockEffectFactory = BlockEffectFactory()
        val furnitureItemComparatorFactory = FurnitureItemComparatorFactory()
        val perItemParameterFactory = PerItemParameterFactory()
        val hitboxFactory = HitboxFactory()
        val currencyFactory = CurrencyFactory()
        val chatChannelFactory = ChatChannelFactory()
        val noteblockTypeFactory = NoteblockTypeFactory()
    }

}