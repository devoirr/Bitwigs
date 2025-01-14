package dev.devoirr.bitwigs.core.chat.model

import dev.devoirr.bitwigs.core.getStringOrNull
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

data class ChatChannel(val format: String, val range: Int?, val symbol: Char?, val permission: String?) {

    companion object {
        fun parse(section: ConfigurationSection): ChatChannel {
            val format = section.getString("format", "%player_name%: <message>")!!
            val range: Int? = if (section.getKeys(false).contains("range")) section.getInt("range") else null
            val symbol = section.getString("symbol")?.toCharArray()?.get(0)
            val permission = section.getStringOrNull("permission")

            return ChatChannel(format, range, symbol, permission)
        }
    }

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
