package dev.devoirr.bitwigs.core

import co.aikar.commands.Locales
import co.aikar.commands.MessageType
import co.aikar.commands.PaperCommandManager
import com.github.retrooper.packetevents.PacketEvents
import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.block.noteblocks.NoteblocksManager
import dev.devoirr.bitwigs.core.chat.ChatManager
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.economy.EconomyManager
import dev.devoirr.bitwigs.core.gui.listener.MenuListener
import dev.devoirr.bitwigs.core.messages.Messages
import dev.devoirr.bitwigs.core.messages.ReloadMessagesCommand
import dev.devoirr.bitwigs.core.tasks.TaskManager
import dev.devoirr.bitwigs.core.test.TestCommand
import dev.devoirr.bitwigs.core.warps.WarpsManager
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class BitwigsPlugin : JavaPlugin() {

    lateinit var commandManager: PaperCommandManager

    lateinit var taskManager: TaskManager

    var furnitureManager: FurnitureManager? = null
    var economyManager: EconomyManager? = null
    var warpManager: WarpsManager? = null
    var chatManager: ChatManager? = null
    var noteblocksManager: NoteblocksManager? = null

    lateinit var uniqueServerId: String

    override fun onEnable() {
        print("Enabled Bitwigs!")

        saveConfigFiles()

        uniqueServerId = config.getString("unique-server-id")!!

        commandManager = PaperCommandManager(this)
        commandManager.locales.setDefaultLocale(Locales.RUSSIAN)
        commandManager.setFormat(MessageType.ERROR, ChatColor.WHITE, ChatColor.RED, ChatColor.RED, ChatColor.RED)
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.WHITE, ChatColor.GRAY, ChatColor.GRAY, ChatColor.GRAY)
        commandManager.setFormat(MessageType.INFO, ChatColor.WHITE, ChatColor.GRAY, ChatColor.GRAY)

        taskManager = TaskManager(this)

        if (config.getBoolean("furniture.enabled", false)) {
            try {
                furnitureManager = FurnitureManager(this)
                furnitureManager!!.onEnable()
            } catch (e: Exception) {
                logger.info("Failed to enable furniture...")
                e.printStackTrace()
                furnitureManager = null
            }
        }

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

        if (config.getBoolean("noteblocks.enabled", false)) {
            try {
                noteblocksManager = NoteblocksManager(this)
                noteblocksManager!!.onEnable()
            } catch (e: Exception) {
                logger.info("Failed to enabled noteblocks...")
                e.printStackTrace()
                noteblocksManager = null
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
        furnitureManager?.onDisable()
        economyManager?.onDisable()
        chatManager?.onDisable()
        noteblocksManager?.onDisable()

        PacketEvents.getAPI().terminate()
    }

    override fun onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load()
    }

}