package dev.devoirr.bitwigs.core

import co.aikar.commands.Locales
import co.aikar.commands.PaperCommandManager
import com.github.retrooper.packetevents.PacketEvents
import dev.devoirr.bitwigs.core.blocks.ReplacedBlocks
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.gui.MenuListener
import dev.devoirr.bitwigs.core.messages.Messages
import dev.devoirr.bitwigs.core.messages.ReloadMessagesCommand
import dev.devoirr.bitwigs.core.module.ModuleCenter
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class BitwigsPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: BitwigsPlugin
            private set
    }

    lateinit var commandManager: PaperCommandManager
    lateinit var uniqueServerId: String

    private lateinit var moduleCenter: ModuleCenter

    override fun onEnable() {
        print("Enabled Bitwigs!")

        instance = this

        moduleCenter = ModuleCenter()

        saveConfigFiles()

        uniqueServerId = config.getString("unique-server-id")!!

        commandManager = PaperCommandManager(this)
        commandManager.locales.setDefaultLocale(Locales.RUSSIAN)

        this.registerCompletions()
        this.commandManager.registerCommand(ReloadMessagesCommand(this))

        BitwigsPlaceholderExpansion(this).register()

        PacketEvents.getAPI().init()

        moduleCenter.loadModules()

        MenuListener().register()
    }

    private fun registerCompletions() {
        /* Later add vanished players support */
        commandManager.commandCompletions.registerCompletion("visible") {
            Bukkit.getOnlinePlayers().map { it.name }
        }
    }

    private fun saveConfigFiles() {
        saveDefaultConfig()

        saveResource("economy.yml", false)
        saveResource("chat.yml", false)
        saveResource("dropping_blocks.yml", false)

        if (!config.getKeys(false).contains("unique-server-id")) {
            config.set("unique-server-id", UUID.randomUUID().toString().split("-")[0])
            saveConfig()
        }

        saveResource("messages.yml", false)
        val messagesConfig = Config(File(this.dataFolder, "messages.yml"))
        Messages.load(messagesConfig)
    }

    override fun onDisable() {
        ReplacedBlocks.onDisable()
        PacketEvents.getAPI().terminate()
    }

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this))
        PacketEvents.getAPI().load()
    }

}