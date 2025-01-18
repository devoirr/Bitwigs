package dev.devoirr.bitwigs.core.chat.model

import dev.devoirr.bitwigs.core.getStringOrNull
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

/**
 *  A "channel" in game-chat.
 *
 * @param format The visual format for the message. Supports placeholders.
 * @param range A range for the messages. If null, the message will be sent to everyone.
 * @param symbol The prefix for identifying the channel.
 * @param permission If specified, only players with this permission can send and recieve messages from this channel
 */
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
