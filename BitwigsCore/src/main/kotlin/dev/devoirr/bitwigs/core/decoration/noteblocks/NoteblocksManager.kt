package dev.devoirr.bitwigs.core.decoration.noteblocks

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.BlockEffect
import dev.devoirr.bitwigs.core.centralize
import dev.devoirr.bitwigs.core.config.Config
import dev.devoirr.bitwigs.core.decoration.DecorationMechanic
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.model.Tool
import dev.devoirr.bitwigs.core.decoration.noteblocks.listener.NoteblockListener
import dev.devoirr.bitwigs.core.decoration.noteblocks.listener.NoteblockPlaceListener
import dev.devoirr.bitwigs.core.decoration.noteblocks.model.NoteblockSubType
import dev.devoirr.bitwigs.core.decoration.noteblocks.model.NoteblockType
import dev.devoirr.bitwigs.core.decoration.noteblocks.packet.NoteblockBreakListener
import dev.devoirr.bitwigs.core.getTool
import dev.devoirr.bitwigs.core.module.Loadable
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import java.io.File
import kotlin.math.pow

class NoteblocksManager : Loadable, DecorationMechanic {

    override fun getName() = "noteblocks"

    private val plugin = BitwigsPlugin.instance
    private val config = Config(File(plugin.dataFolder, "noteblocks.yml"))

    private val types = mutableListOf<NoteblockType>()

    private val placeListener = NoteblockPlaceListener(this)
    private val fixListener = NoteblockListener(this)
    private val breakingListener = NoteblockBreakListener(this)

    override fun onEnable() {

        config.get().getConfigurationSection("blocks")?.getKeys(false)?.forEach { key ->
            config.get().getConfigurationSection("blocks.$key")?.let { section ->
                types.add(NoteblockType.parse(section))
            }
        }

        placeListener.register()
        fixListener.register()

        PacketEvents.getAPI().eventManager.registerListener(breakingListener, PacketListenerPriority.HIGH)

    }

    override fun onDisable() {
        HandlerList.unregisterAll(placeListener)
        HandlerList.unregisterAll(fixListener)
    }

    fun getTypeAndSubtype(itemStack: ItemStack): Pair<NoteblockType, NoteblockSubType>? {
        for (type in types) {
            type.subTypes.firstOrNull { it.isThisType(itemStack) }?.let {
                return Pair(type, it)
            }
        }

        return null
    }

    fun getTypeAndSubtype(block: Block): Pair<NoteblockType, NoteblockSubType>? {
        for (type in types) {
            type.subTypes.firstOrNull { it.isThisType(block) }?.let {
                return Pair(type, it)
            }
        }

        return null
    }

    override fun isThisMechanic(block: Block): Boolean {
        return block.type == Material.NOTE_BLOCK
    }

    override fun getEffect(block: Block, interactionType: InteractionType): BlockEffect? {
        if (block.type != Material.NOTE_BLOCK)
            return null

        val typePair = getTypeAndSubtype(block) ?: return null
        val type = typePair.first
        return type.effects[interactionType]
    }

    override fun breakBlock(block: Block, player: Player, itemStack: ItemStack) {
        val typeAndSubType = getTypeAndSubtype(block) ?: return
        val subtype = typeAndSubType.second

        block.type = Material.AIR

        val shouldDrop =
            itemStack.getTool()?.name in typeAndSubType.first.toolsToDrop || itemStack.type.name in typeAndSubType.first.toolsToDrop
        if (shouldDrop) {
            block.world.dropItemNaturally(block.location.centralize(), subtype.createItemStack())
        }
    }

    override fun getPeriodForBlock(block: Block, itemStack: ItemStack): Long {
        if (block.type != Material.NOTE_BLOCK)
            return 0L

        val typePair = getTypeAndSubtype(block) ?: return 0L
        val type = typePair.first

        val period = type.hardness
        var modifier = 1.0

        if (type.isThisTool(itemStack)) {
            modifier *= 0.4
            val hierarchy = Tool.getPriority(itemStack)
            if (hierarchy >= 1) {
                modifier *= 0.9.pow(hierarchy.toDouble())
            }
        }

        return modifier.toLong() * period
    }
}