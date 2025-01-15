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
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission
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

    var permission: Permission? = null
        private set
    var chat: Chat? = null
        private set
    var economy: Economy? = null
        private set

    override fun onEnable() {
        print("Enabled Bitwigs!")

        instance = this

        this.moduleCenter = ModuleCenter()

        this.saveConfigFiles()

        this.uniqueServerId = config.getString("unique-server-id")!!

        this.commandManager = PaperCommandManager(this)
        this.commandManager.locales.setDefaultLocale(Locales.RUSSIAN)

        this.registerCompletions()
        this.commandManager.registerCommand(ReloadMessagesCommand(this))

        this.loadVault()

        BitwigsPlaceholderExpansion(this).register()

        PacketEvents.getAPI().init()
        MenuListener().register()

        this.moduleCenter.loadModules()
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
        saveResource("action_blocks.yml", false)

        if (!config.getKeys(false).contains("unique-server-id")) {
            config.set("unique-server-id", UUID.randomUUID().toString().split("-")[0])
            saveConfig()
        }

        saveResource("messages.yml", false)
        val messagesConfig = Config(File(this.dataFolder, "messages.yml"))
        Messages.load(messagesConfig)
    }

    private fun loadVault() {

        if (!Bukkit.getPluginManager().isPluginEnabled("Vault"))
            return

        permission = server.servicesManager.getRegistration(Permission::class.java)?.provider
        chat = server.servicesManager.getRegistration(Chat::class.java)?.provider
        economy = server.servicesManager.getRegistration(Economy::class.java)?.provider

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