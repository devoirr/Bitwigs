package dev.devoirr.bitwigs.core.menu.container

import net.kyori.adventure.text.Component

data class MenuParameters(
    val title: Component,
    val size: Int,
    val fillerSlots: List<Int> = mutableListOf(),
    val clickable: Boolean = false
)
