package dev.devoirr.bitwigs.core.block.furniture.listener

import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.block.furniture.model.FurnitureBreakingTask
import dev.devoirr.bitwigs.core.block.furniture.model.FurnitureType
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageAbortEvent
import org.bukkit.event.block.BlockDamageEvent
import java.util.*

class FurnitureBreakListener(private val manager: FurnitureManager) : Listener {

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {

        if (event.player.gameMode != GameMode.CREATIVE)
            return

        val block = event.block
        if (!block.hasMetadata("furniture"))
            return

        val furnitureId = block.getMetadata("furniture")[0].asInt()

        val furniture =
            manager.placedFurnitureHolder.get(furnitureId) ?: return

        event.isCancelled = true
        val furnitureType = manager.getFurnitureType(furniture.type) ?: return

        playBreakEffect(furnitureType, event.player, furniture.center)

        manager.placedFurnitureHolder.destroy(furniture)

    }

    @EventHandler
    fun onStart(event: BlockDamageEvent) {

        if (event.block.type != Material.BARRIER)
            return

        if (event.player.gameMode == GameMode.CREATIVE)
            return

        val block = event.block
        if (!block.hasMetadata("furniture"))
            return

        val furnitureId = block.getMetadata("furniture")[0].asInt()

        val furniture =
            manager.placedFurnitureHolder.get(furnitureId) ?: return
        val furnitureType = manager.getFurnitureType(furniture.type) ?: return

        event.isCancelled = true

        val item = event.player.inventory.itemInMainHand
        val breakTime = furnitureType.breakTime.get(item.type).toInt()

        if (breakTime == 0) {
            playBreakEffect(furnitureType, event.player, furniture.center)

            manager.placedFurnitureHolder.destroy(furniture)
            return
        }

        val key = "${event.player.name}-${UUID.randomUUID()}"
        FurnitureBreakingTask.registerKey(key)

        FurnitureBreakingTask(
            event.player,
            key,
            furniture,
            furnitureType,
            manager,
            (breakTime.toDouble() / 10).toInt(),
            { playBreakEffect(furnitureType, event.player, block) }
        ).run()
    }

    @EventHandler
    fun onCancel(event: BlockDamageAbortEvent) {
        if (event.block.type != Material.BARRIER)
            return

        if (event.player.gameMode == GameMode.CREATIVE)
            return

        val block = event.block
        if (!block.hasMetadata("furniture"))
            return

        FurnitureBreakingTask.removeKey(event.player)
    }

    private fun playBreakEffect(
        furnitureType: FurnitureType,
        player: Player,
        center: Block
    ) {
        furnitureType.breakEffect?.let { effect ->
            effect.sound?.let { player.playSound(player, it, 1f, 1f) }
            effect.particle?.let {
                center.world.spawnParticle(it, center.location.clone().add(0.5, 0.5, 0.5), 10)
            }
        }
    }

}