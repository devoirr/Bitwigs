package dev.devoirr.bitwigs.core.chat.model

import org.bukkit.Bukkit
import org.bukkit.entity.Player

data class ChatChannel(val format: String, val range: Int?, val symbol: Char?, val permission: String?) {

    fun isThisChannel(text: String): Boolean {
        if (symbol == null)
            return false
        return text.startsWith(symbol)
    }

    fun getRecipients(sender: Player): List<Player> {
        val list = mutableListOf<Player>()
        list.addAll(Bukkit.getOnlinePlayers())

        if (range != null) {
            if (range > 0) {
                list.removeIf { it.world.name != sender.world.name }
                list.removeIf { it.location.distance(sender.location) > range }
            } else if (range == -1) {
                list.removeIf { it.world.name != sender.world.name }
            }
        }

        permission?.let {
            list.removeIf { !it.hasPermission(permission) }
        }

        return list
    }

}
