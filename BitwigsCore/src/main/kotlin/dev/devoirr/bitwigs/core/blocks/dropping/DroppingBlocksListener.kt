package dev.devoirr.bitwigs.core.blocks.dropping

import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.blocks.dropping.model.database.PlacedDroppingBlockRow
import dev.devoirr.bitwigs.core.blocks.dropping.model.event.DroppingBlockBreakEvent
import dev.devoirr.bitwigs.core.blocks.dropping.model.event.DroppingBlockStartLootEvent
import dev.devoirr.bitwigs.core.blocks.dropping.model.task.LootingTask
import dev.devoirr.bitwigs.core.toString
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable

class DroppingBlocksListener(private val manager: DroppingBlocksManager) : Listener {

    private val lootInProcess = mutableListOf<Block>()
    private val refillInProcess = mutableListOf<Block>()

    /* Handle block breaking. If not canBeBroken -> cancel, else -> call DroppingBlockBreakEvent, and cancel accordingly. */
    @EventHandler
    fun onBreak(event: BlockBreakEvent) {

        val block = event.block
        if (!block.hasMetadata("dropping_block"))
            return

        val type = manager.getType(block.getMetadata("dropping_block")[0].asString()) ?: return
        if (!type.canBeBroken) {
            event.isCancelled = true
            return
        }

        val breakEvent = DroppingBlockBreakEvent(event.player, type, event.block)
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
    fun onDroppingBlockBreak(event: DroppingBlockBreakEvent) {
        val row = PlacedDroppingBlockRow()
        row.location = event.block.location.toString(block = true)
        row.type = event.type.key

        manager.database.delete(row)
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {

        if (!event.action.isRightClick)
            return

        if (!event.hasBlock())
            return

        val block = event.clickedBlock ?: return
        if (!block.hasMetadata("dropping_block"))
            return

        val type = manager.getType(block.getMetadata("dropping_block")[0].asString()) ?: return
        event.isCancelled = true

        if (block.hasMetadata("looting") || block.hasMetadata("refilling"))
            return

        val startLootEvent = DroppingBlockStartLootEvent(event.player, block, type)

        object : BukkitRunnable() {
            override fun run() {
                Bukkit.getPluginManager().callEvent(startLootEvent)
            }
        }.runTask(BitwigsPlugin.instance)

        if (startLootEvent.isCancelled) {
            return
        }

        LootingTask(block, type, type.lootTime)
            .runTaskTimer(BitwigsPlugin.instance, 0L, 20L)

    }

}