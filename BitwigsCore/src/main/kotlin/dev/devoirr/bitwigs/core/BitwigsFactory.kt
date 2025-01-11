package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.blocks.BlockEffectFactory
import dev.devoirr.bitwigs.core.blocks.dropping.factory.DroppingBlockItemFactory
import dev.devoirr.bitwigs.core.blocks.dropping.factory.DroppingBlockTypeFactory
import dev.devoirr.bitwigs.core.chat.model.factory.ChatChannelFactory
import dev.devoirr.bitwigs.core.database.DatabaseInfoFactory
import dev.devoirr.bitwigs.core.economy.model.factory.CurrencyFactory
import dev.devoirr.bitwigs.core.effect.particle.ParticleInfoFactory
import dev.devoirr.bitwigs.core.effect.sound.SoundInfoFactory
import dev.devoirr.bitwigs.core.item.factory.ItemComparatorFactory

class BitwigsFactory {

    companion object {
        val databaseInfoFactory = DatabaseInfoFactory()
        val currencyFactory = CurrencyFactory()
        val chatChannelFactory = ChatChannelFactory()
        val itemComparatorFactory = ItemComparatorFactory()
        val soundInfoFactory = SoundInfoFactory()
        val particleInfoFactory = ParticleInfoFactory()
        val droppingBlockItemFactory = DroppingBlockItemFactory()
        val blockEffectFactory = BlockEffectFactory()
        val droppingBlockTypeFactory = DroppingBlockTypeFactory()
    }

}