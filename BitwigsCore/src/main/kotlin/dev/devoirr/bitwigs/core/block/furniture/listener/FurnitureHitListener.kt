package dev.devoirr.bitwigs.core.block.furniture.listener

import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.block.furniture.model.FurnitureType
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class FurnitureHitListener(private val manager: FurnitureManager) : Listener {

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        if (!event.action.isLeftClick)
            return

        if (!event.hasBlock())
            return

        val block = event.clickedBlock!!
        if (!block.hasMetadata("furniture"))
            return

        val furnitureId = block.getMetadata("furniture")[0].asInt()
        val furniture =
            manager.placedFurnitureHolder.get(furnitureId) ?: return
        val furnitureType = manager.getFurnitureType(furniture.type) ?: return

        playEffect(furnitureType, event.player, block)
    }

    private fun playEffect(
        furnitureType: FurnitureType,
        player: Player,
        center: Block
    ) {
        furnitureType.hitEffect?.let { effect ->
            effect.sound?.let { player.playSound(player, it, 1f, 1f) }
            effect.particle?.let {
                center.world.spawnParticle(it, center.location.clone().add(0.5, 0.5, 0.5), 10)
            }
        }
    }

}