package dev.devoirr.bitwigs.core.chat

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.chat.listener.ChatListener
import dev.devoirr.bitwigs.core.chat.model.ChatChannel
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.module.Loadable
import org.bukkit.event.HandlerList
import java.io.File

class ChatManager : Loadable {

    private val plugin = BitwigsPlugin.instance
    private val config = Config(File(plugin.dataFolder, "chat.yml"))

    lateinit var chatRegex: Regex

    private val channels = mutableListOf<ChatChannel>()
    private var defaultChannel: ChatChannel? = null

    var minLength: Int = 1
    var maxLength: Int = 100

    private lateinit var listener: ChatListener

    override fun getName(): String {
        return "chat"
    }

    override fun onEnable() {

        chatRegex = Regex("[а-яА-ЯёЁA-Za-z0-9-~!@#\$%^&*()<>/_+=-{}|';:.,\\[\"\\] ]+\$")

        val defaultChannelName = config.get().getString("default-channel", "local")!!

        var channelSection = config.get().getConfigurationSection("channels")
        if (channelSection == null) {
            channelSection = config.get().createSection("channels")
            config.save()
        }

        var channel: ChatChannel
        for (channelName in config.get().getConfigurationSection("channels")!!.getKeys(false)) {
            channel = ChatChannel.parse(channelSection.getConfigurationSection(channelName)!!)
            channels.add(channel)

            if (defaultChannelName == channelName) {
                defaultChannel = channel
            }
        }

        minLength = config.get().getInt("min-length", 1)
        maxLength = config.get().getInt("max-length", 1)

        listener = ChatListener(this)
        plugin.server.pluginManager.registerEvents(listener, plugin)
    }

    override fun onDisable() {
        HandlerList.unregisterAll(listener)
    }

    fun getChannel(text: String): ChatChannel? {
        return channels.firstOrNull { it.isThisChannel(text) } ?: defaultChannel
    }

    fun getDefault() = defaultChannel

}