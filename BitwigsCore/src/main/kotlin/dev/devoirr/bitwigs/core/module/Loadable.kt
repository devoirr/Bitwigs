package dev.devoirr.bitwigs.core.module

interface Loadable {

    fun getName(): String

    fun onEnable()
    fun onDisable()

}