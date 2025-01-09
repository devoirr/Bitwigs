package dev.devoirr.bitwigs.core.decoration.furniture.listener

import com.google.common.cache.CacheBuilder
import dev.devoirr.bitwigs.core.BitwigsPlugin
import dev.devoirr.bitwigs.core.decoration.InteractionType
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.decoration.furniture.FurnitureType
import dev.devoirr.bitwigs.core.decoration.furniture.event.FurniturePlaceEvent
import dev.devoirr.bitwigs.core.decoration.furniture.placed.PlacedFurniture
import dev.devoirr.bitwigs.core.toBase64
import dev.devoirr.bitwigs.core.util.BlockFaceUtility
import dev.devoirr.bitwigs.core.withAmount
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import java.time.Duration
import java.util.*

class FurniturePlaceListener(private val manager: FurnitureManager) : Listener {

    private val cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMillis(20)).build<UUID, Byte>()

    /* Placement handler */
    @EventHandler
    fun onClick(event: PlayerInteractEvent) {

        if (cache.getIfPresent(event.player.uniqueId) != null)
            return

        if (!event.action.isRightClick)
            return

        if (!event.hasItem() || !event.hasBlock())
            return

        val block = event.clickedBlock ?: return
        val item = event.item ?: return

        val type = manager.getFurnitureType(item) ?: return

        event.isCancelled = true

        val target = if (block.isReplaceable) block else block.getRelative(event.blockFace)
        if (type.allowPlaceOn.isNotEmpty()) {
            val placingOn = target.getRelative(event.blockFace.oppositeFace).type
            if (placingOn.name !in type.allowPlaceOn)
                return
        }

        val placeEvent = FurniturePlaceEvent(event.player, target, type)
        Bukkit.getPluginManager().callEvent(placeEvent)

        if (placeEvent.isCancelled)
            return

        handlePlacement(target, event.player, item, type)

    }

    private fun handlePlacement(center: Block, player: Player, item: ItemStack, type: FurnitureType) {

        val direction = BlockFaceUtility.getClosestBlockface(player.yaw)

        val blocks = type.hitbox.getBlocks(center, direction, true)
        if (blocks.any { it.type != Material.AIR && it.type != Material.LIGHT }) {
            return
        }

        val id = manager.placedFurnitureHolder.createId()
        for (tempBlock in blocks) {
            tempBlock.type = Material.BARRIER
            tempBlock.setMetadata("furniture", FixedMetadataValue(BitwigsPlugin.instance, id))
        }

        type.effects[InteractionType.PLACE]?.let { effect ->
            blocks.forEach { effect.play(it) }
        }

        val location = center.location.clone().add(0.5, 0.5, 0.5)
        if (player.isSneaking && type.rotatable) {
            val standDirection = player.location.toVector().subtract(location.toVector())
            location.setDirection(standDirection)
        } else {
            location.setDirection(player.facing.oppositeFace.direction)
        }
        val placedFurniture = PlacedFurniture(
            manager,
            type.key,
            center,
            direction,
            location.yaw,
            id,
            item.clone().withAmount(1).toBase64()
        )

        placedFurniture.sendCreationForWorld()
        manager.placedFurnitureHolder.register(placedFurniture)

        item.amount--
        player.setCooldown(item.type, 2)

        cache.put(player.uniqueId, 0)

    }

}