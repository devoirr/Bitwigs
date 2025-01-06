package dev.devoirr.bitwigs.core.messages

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.config.Config
import org.bukkit.command.CommandSender
import java.io.File

class ReloadMessagesCommand(private val plugin: BitwigsPlugin) : BaseCommand() {

    @CommandAlias("reloadmessages")
    @CommandPermission("bitwigs.command.messages.reload")
    fun reloadMessages(sender: CommandSender, args: Array<String>) {

        val messagesConfig = Config(File(this.plugin.dataFolder, "messages.yml"))
        Messages.load(messagesConfig)

        Messages.MESSAGES_RELOADED.getInfo().sendTo(sender)
    }

}