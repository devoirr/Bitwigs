package dev.devoirr.bitwigs.core.crates

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.module.Loadable

class CratesModule : Loadable {

    private val plugin = BitwigsPlugin.instance

    override fun getName(): String {
        return "crates"
    }

    override fun onEnable() {

    }

    override fun onDisable() {

    }
}