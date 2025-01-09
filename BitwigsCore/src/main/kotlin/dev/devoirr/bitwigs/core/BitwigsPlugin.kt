package dev.devoirr.bitwigs.core

import co.aikar.commands.Locales
import co.aikar.commands.PaperCommandManager
import com.github.retrooper.packetevents.PacketEvents
import dev.devoirr.bitwigs.core.chat.ChatManager
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.decoration.furniture.sitting.Sitting
import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.gui.listener.MenuListener
import dev.devoirr.bitwigs.core.messages.Messages
import dev.devoirr.bitwigs.core.messages.ReloadMessagesCommand
import dev.devoirr.bitwigs.core.sound.SoundInfo
import dev.devoirr.bitwigs.core.test.TestCommand
import dev.devoirr.bitwigs.core.warps.WarpsManager
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class BitwigsPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: BitwigsPlugin
            private set
    }

    lateinit var commandManager: PaperCommandManager

    var economyManager: EconomyManager? = null
    private var warpManager: WarpsManager? = null
    private var chatManager: ChatManager? = null

    lateinit var uniqueServerId: String

    override fun onEnable() {
        print("Enabled Bitwigs!")

        instance = this

        saveConfigFiles()

        uniqueServerId = config.getString("unique-server-id")!!

        commandManager = PaperCommandManager(this)
        commandManager.locales.setDefaultLocale(Locales.RUSSIAN)

        ConfigurationSerialization.registerClass(SoundInfo::class.java)
        ConfigurationSerialization.registerClass(Sitting::class.java)

        if (config.getBoolean("economy.enabled", false)) {
            try {
                economyManager = EconomyManager(this)
                economyManager!!.onEnable()
            } catch (e: Exception) {
                logger.info("Failed to enable economy...")
                e.printStackTrace()
                economyManager = null
            }
        }

        if (config.getBoolean("warps.enabled", false)) {
            try {
                warpManager = WarpsManager(this)
                warpManager!!.onEnable()
            } catch (e: Exception) {
                logger.info("Failed to enable warps...")
                e.printStackTrace()
                warpManager = null
            }
        }

        if (config.getBoolean("chat.enabled", false)) {
            try {
                chatManager = ChatManager(this)
                chatManager!!.onEnable()
            } catch (e: Exception) {
                logger.info("Failed to enable chat...")
                e.printStackTrace()
                chatManager = null
            }
        }

        server.pluginManager.registerEvents(MenuListener(), this)

        this.registerCompletions()
        this.commandManager.registerCommand(ReloadMessagesCommand(this))
        this.commandManager.registerCommand(TestCommand())

        BitwigsPlaceholderExpansion(this).register()

        PacketEvents.getAPI().init()

    }

    private fun registerCompletions() {
        commandManager.commandCompletions.registerCompletion("currencies") {
            economyManager?.getAllCurrencyKeys() ?: emptyList<String>()
        }
        commandManager.commandCompletions.registerCompletion("visible") {
            Bukkit.getOnlinePlayers().map { it.name }
        }
        commandManager.commandCompletions.registerCompletion("warps") {
            warpManager?.getAllWarpNames() ?: emptyList<String>()
        }
    }

    private fun saveConfigFiles() {
        saveDefaultConfig()

        saveResource("furniture.yml", false)
        saveResource("economy.yml", false)
        saveResource("chat.yml", false)
        saveResource("noteblocks.yml", false)

        if (!config.getKeys(false).contains("unique-server-id")) {
            config.set("unique-server-id", UUID.randomUUID().toString().split("-")[0])
            saveConfig()
        }

        saveResource("messages.yml", false)
        val messagesConfig = Config(File(this.dataFolder, "messages.yml"))
        Messages.load(messagesConfig)
    }

    override fun onDisable() {
        economyManager?.onDisable()
        chatManager?.onDisable()

        PacketEvents.getAPI().terminate()
    }

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

}