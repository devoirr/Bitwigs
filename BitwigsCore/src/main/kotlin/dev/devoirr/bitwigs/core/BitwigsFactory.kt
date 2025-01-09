package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.chat.model.factory.ChatChannelFactory
import dev.devoirr.bitwigs.core.database.DatabaseInfoFactory
import dev.devoirr.bitwigs.core.decoration.model.factory.BlockEffectFactory
import dev.devoirr.bitwigs.core.decoration.model.factory.HitboxFactory
import dev.devoirr.bitwigs.core.economy.model.factory.CurrencyFactory
import dev.devoirr.bitwigs.core.item.factory.ItemComparatorFactory

class BitwigsFactory {

    companion object {
        val databaseInfoFactory = DatabaseInfoFactory()
        val currencyFactory = CurrencyFactory()
        val chatChannelFactory = ChatChannelFactory()
        val itemComparatorFactory = ItemComparatorFactory()
        val hitboxFactory = HitboxFactory()
        val blockEffectFactory = BlockEffectFactory()
    }

}