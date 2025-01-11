package dev.devoirr.bitwigs.core.chat.model.factory

import dev.devoirr.bitwigs.core.chat.model.ChatChannel
import dev.devoirr.bitwigs.core.getStringOrNull
import dev.devoirr.bitwigs.core.util.factory.Factory
import org.bukkit.configuration.ConfigurationSection

class ChatChannelFactory : Factory<ChatChannel> {

    override fun parse(section: ConfigurationSection): ChatChannel {
        val format = section.getString("format", "%player_name%: <message>")!!
        val range: Int? = if (section.getKeys(false).contains("range")) section.getInt("range") else null
        val symbol = section.getString("symbol")?.toCharArray()?.get(0)
        val permission = section.getStringOrNull("permission")

        return ChatChannel(format, range, symbol, permission)
    }

    override fun write(item: ChatChannel, section: ConfigurationSection) {
        
    }
}