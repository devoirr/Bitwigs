package dev.devoirr.bitwigs.core.gui

import org.bukkit.entity.Player
import java.util.*

object MenuManager {

    private val menus = mutableMapOf<UUID, Menu>()
    private val opened = mutableMapOf<UUID, MenuHolder>()

    fun addMenuHolderForPlayer(player: Player, holder: MenuHolder) {
        opened[player.uniqueId] = holder
    }

    fun closeMenuHolderForPlayer(player: Player) {
        opened.remove(player.uniqueId)
    }

    fun getPlayerMenuHolder(player: Player) = opened[player.uniqueId]

    fun getMenu(uuid: UUID): Menu? {
        return menus[uuid]
    }

    fun registerMenu(menu: Menu, uuid: UUID) {
        menus[uuid] = menu
    }

}