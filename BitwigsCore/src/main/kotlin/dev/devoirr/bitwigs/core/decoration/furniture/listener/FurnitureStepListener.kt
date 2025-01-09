package dev.devoirr.bitwigs.core.decoration.furniture.listener

import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import org.bukkit.GameEvent
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.GenericGameEvent

class FurnitureStepListener(private val manager: FurnitureManager) : Listener {

    /* Step handler */
    @EventHandler
    fun onStep(event: GenericGameEvent) {
        if (event.event != GameEvent.STEP)
            return

        val entity = event.entity ?: return
        if (entity.type != EntityType.PLAYER)
            return

        val player = entity as Player

        val block = getBlockBelowPlayer(player)
        if (block.type != Material.BARRIER)
            return

        if (!block.hasMetadata("furniture"))
            return

        val furnitureId = block.getMetadata("furniture")[0].asInt()
        val furniture =
            manager.placedFurnitureHolder.get(furnitureId) ?: return
        val furnitureType = manager.getFurnitureType(furniture.type) ?: return

        furnitureType.effects[InteractionType.STEP]?.play(block)
    }

    private fun getBlockBelowPlayer(player: Player): Block {
        return player.location.block.getRelative(BlockFace.DOWN)
    }

}