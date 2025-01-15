package dev.devoirr.bitwigs.core.blocks.clickable

import com.google.common.cache.CacheBuilder
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.ReplacedBlockData
import dev.devoirr.bitwigs.core.blocks.ReplacedBlocks
import dev.devoirr.bitwigs.core.blocks.clickable.model.event.ActionBlockBreakEvent
import dev.devoirr.bitwigs.core.cooldown.CooldownManager
import dev.devoirr.bitwigs.core.getGroup
import dev.devoirr.bitwigs.core.getLeft
import dev.devoirr.bitwigs.core.listener.Listener
import dev.devoirr.bitwigs.core.toComponent
import dev.devoirr.bitwigs.core.util.TextUtility
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Note
import org.bukkit.block.data.type.NoteBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration

class ActionBlockListener(private val manager: ActionBlocksManager) : Listener() {

    private val cooldowns = mutableMapOf<Location, Long>()
    private val cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMillis(100)).build<String, Byte>()

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {

        val block = event.block
        if (!block.hasMetadata("action_block"))
            return

        val type = manager.getType(block.getMetadata("action_block")[0].asString()) ?: return
        val breakEvent = ActionBlockBreakEvent(event.player, type, event.block)
        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getPluginManager().callEvent(event)
            }
        }.runTask(BitwigsPlugin.instance)

        if (breakEvent.isCancelled) {
            event.isCancelled = true
        }

    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {

        if (cache.asMap().contains(event.player.name))
            return

        if (EquipmentSlot.OFF_HAND == event.hand)
            return

        if (!event.action.isRightClick)
            return

        if (!event.hasBlock())
            return

        val block = event.clickedBlock ?: return
        if (!block.hasMetadata("action_block"))
            return

        val type = manager.getType(block.getMetadata("action_block")[0].asString()) ?: return
        event.isCancelled = true

        if (block.hasMetadata("cooldown"))
            return
        
        val player = event.player
        cache.put(player.name, 0x0)

        if (type.permission != null) {
            if (!player.hasPermission(type.permission)) {
                type.permissionMessage?.let { player.sendMessage(it.toComponent()) }
                return
            }
        }

        if (type.hasPerPlayerCooldown()) {
            val left = player.getLeft(type.perPlayerCooldown!!)
            if (left != null) {
                if (type.perPlayerCooldown.message != null) {
                    player.sendMessage(type.perPlayerCooldown.message.replace("<left>", left).toComponent())
                }
                return
            }
        }

        if (type.hasGlobalCooldown()) {
            if (block.location in cooldowns.keys) {
                val left = cooldowns[block.location]!! - System.currentTimeMillis()
                if (left < 0) {
                    cooldowns.remove(block.location)
                } else {
                    if (type.globalCooldown!!.message != null) {
                        player.sendMessage(
                            type.globalCooldown.message!!.replace("<left>", TextUtility.millisToTime(left))
                                .toComponent()
                        )
                    }

                    return
                }
            }
        }

        type.runFor(player, block)

        if (type.hasPerPlayerCooldown()) {
            CooldownManager.addToPlayer(player, type.perPlayerCooldown!!)
        }

        if (type.hasGlobalCooldown()) {
            val forGroup = type.globalCooldown!!.getFor(player.getGroup())

            if (forGroup <= 0)
                return

            cooldowns[block.location] = System.currentTimeMillis() + forGroup * 1000L

            val defaultType = block.type
            val defaultBlockData = block.blockData

            if (type.globalCooldownReplacer != null) {
                val filler = type.globalCooldownReplacer
                block.type = filler.material
                if (block.type == Material.NOTE_BLOCK && filler.note != null && filler.isPowered != null && filler.instrument != null) {
                    val data = block.blockData as NoteBlock

                    data.note = Note(filler.note)
                    data.instrument = filler.instrument
                    data.isPowered = filler.isPowered

                    ReplacedBlocks.add(ReplacedBlockData(block, defaultType, defaultBlockData))
                }

                object : BukkitRunnable() {
                    override fun run() {
                        block.type = defaultType
                        block.blockData = defaultBlockData

                        ReplacedBlocks.removeBlock(block)
                    }
                }.runTaskLater(BitwigsPlugin.instance, (forGroup) * 20L)
            }
        }

    }

}