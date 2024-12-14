package dev.devoirr.bitwigs.core.gui.session

import dev.devoirr.bitwigs.core.gui.Menu
import org.bukkit.entity.Player

data class MenuSession(val player: Player, val menu: Menu, val started: Long = System.currentTimeMillis())