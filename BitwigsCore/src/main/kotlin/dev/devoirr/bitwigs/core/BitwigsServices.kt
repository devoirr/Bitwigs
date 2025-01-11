package dev.devoirr.bitwigs.core

import dev.devoirr.bitwigs.core.blocks.dropping.DroppingBlocksSerivce
import dev.devoirr.bitwigs.core.economy.EconomyService

class BitwigsServices {

    companion object {

        var economyService: EconomyService? = null
        var droppingBlocksSerivce: DroppingBlocksSerivce? = null
    }

}