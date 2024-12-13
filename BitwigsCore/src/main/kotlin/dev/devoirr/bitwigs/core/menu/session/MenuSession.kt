package dev.devoirr.bitwigs.core.menu.session

import dev.devoirr.bitwigs.core.menu.Menu
import org.bukkit.entity.Player

data class MenuSession(val player: Player, val menu: Menu, val started: Long = System.currentTimeMillis())