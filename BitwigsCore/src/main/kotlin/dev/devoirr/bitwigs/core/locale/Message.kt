package dev.devoirr.bitwigs.core.locale

import dev.devoirr.bitwigs.core.toComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Message(val literal: String) {

    private fun sendLiteral(sender: CommandSender, vararg replacements: Pair<String, String>) {
        var message = literal
        for (replacement in replacements) {
            message = message.replace(replacement.first, replacement.second)
        }

        sender.sendMessage(message.toComponent())
    }

    private fun send(player: Player, vararg replacements: Pair<String, String>) {
        var message = literal
        for (replacement in replacements) {
            message = message.replace(replacement.first, replacement.second)
        }

        if (message.startsWith("-actionbar")) {
            message = message.substring(10)
            player.sendActionBar(message.toComponent())
        } else if (message.startsWith("-title")) {
            message = message.substring(6)

            val args = message.split("\n")
            if (args.size > 1) {
                player.showTitle(Title.title(args[0].toComponent(), args[1].toComponent()))
            } else {
                player.showTitle(Title.title(args[0].toComponent(), Component.empty()))
            }
        } else {
            player.sendMessage(message.toComponent())
        }
    }

    fun send(sender: CommandSender, vararg replacements: Pair<String, String>) {
        if (sender !is Player)
            sendLiteral(sender, *replacements)
        else send(sender, *replacements)

    }

}