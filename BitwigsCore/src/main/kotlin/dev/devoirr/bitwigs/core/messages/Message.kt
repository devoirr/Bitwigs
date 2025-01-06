package dev.devoirr.bitwigs.core.messages

import dev.devoirr.bitwigs.core.toComponent
import org.bukkit.command.CommandSender

class Message(private var text: String, private var actionBar: Boolean = false) {

    fun sendTo(sender: CommandSender) {
        update()
        if (actionBar)
            sender.sendActionBar(text.toComponent())
        else
            sender.sendMessage(text.toComponent())
    }

    fun getInfo(): Message {
        update()
        return Message(Messages.PREFIX_INFO.getLiteral() + text, actionBar)
    }

    fun getError(): Message {
        update()
        return Message(Messages.PREFIX_ERROR.getLiteral() + text, actionBar)
    }

    fun replace(match: String, replacement: String): Message {
        return Message(text.replace(match, replacement), actionBar)
    }

    private fun update() {
        if (text.startsWith("-actionbar ")) {
            text = text.substring(11)
            actionBar = true
        }
    }

}