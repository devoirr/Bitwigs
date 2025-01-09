package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.chat.model.factory.ChatChannelFactory
import dev.devoirr.bitwigs.core.database.DatabaseInfoFactory
import dev.devoirr.bitwigs.core.economy.model.factory.CurrencyFactory

class BitwigsFactory {

    companion object {
        val databaseInfoFactory = DatabaseInfoFactory()
        val currencyFactory = CurrencyFactory()
        val chatChannelFactory = ChatChannelFactory()
    }

}