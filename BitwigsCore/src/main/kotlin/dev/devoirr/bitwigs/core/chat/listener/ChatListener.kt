package dev.devoirr.bitwigs.core.chat.listener

import dev.devoirr.bitwigs.core.chat.ChatManager
import dev.devoirr.bitwigs.core.locale.Locale
import dev.devoirr.bitwigs.core.toComponent
import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ChatListener(private val manager: ChatManager) : Listener {

    @EventHandler
    fun onChat(event: AsyncChatEvent) {

        val player = event.player
        var messageText = LegacyComponentSerializer.legacyAmpersand().serialize(event.message())
        var channel = manager.getChannel(messageText) ?: return

        channel.permission?.let {
            if (!player.hasPermission(it)) {
                channel = manager.getDefault()!!
            }
        }

        if (channel.symbol != null) {
            messageText = messageText.substring(1)
        }

        event.isCancelled = true
        if (messageText.isBlank()) {
            channel.symbol?.let {
                messageText += channel.symbol
            }
        }

        if (messageText.length < manager.minLength) {
            Locale.chatMessageTooShort.send(player)
            return
        }

        if (messageText.length > manager.maxLength) {
            Locale.chatMessageTooLong.send(player)
            return
        }

        if (!messageText.matches(manager.chatRegex)) {
            Locale.chatMessageRegex.send(player)
            return
        }

        event.isCancelled = false
        event.message(messageText.toComponent())

        val format =
            PlaceholderAPI.setPlaceholders(player, channel.format.replace("<message>", messageText)).toComponent()
        event.renderer { _, _, _, _ ->
            return@renderer format
        }

        val recipients = channel.getRecipients(player)
        event.viewers().removeIf { viewer ->
            viewer !is Player || viewer !in recipients
        }
    }

}