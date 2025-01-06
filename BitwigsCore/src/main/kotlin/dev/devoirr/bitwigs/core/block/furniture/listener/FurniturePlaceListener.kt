package dev.devoirr.bitwigs.core.block.furniture.listener

import com.google.common.cache.CacheBuilder
import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.block.furniture.model.FurnitureType
import dev.devoirr.bitwigs.core.block.furniture.model.placed.PlacedFurniture
import dev.devoirr.bitwigs.core.toBase64
import dev.devoirr.bitwigs.core.util.BlockFaceUtility
import dev.devoirr.bitwigs.core.withAmount
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.time.Duration
import java.util.*

class FurniturePlaceListener(private val manager: FurnitureManager) : Listener {

    private val cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMillis(20)).build<UUID, Byte>()

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        if (cache.getIfPresent(event.player.uniqueId) != null)
            return

        if (!event.hasItem())
            return

        if (!event.hasBlock())
            return

        if (!event.action.isRightClick)
            return

        val player = event.player

        val item = event.item ?: return

        val block = event.clickedBlock ?: return

        val furnitureType = manager.getFurnitureType(item) ?: return
        val center = block.getRelative(event.blockFace)

        event.isCancelled = true
        handlePlacement(center, player, item, furnitureType)

        cache.put(player.uniqueId, 0x0)
    }

    private fun handlePlacement(center: Block, player: Player, item: ItemStack, furnitureType: FurnitureType) {

        val direction = BlockFaceUtility.getClosestBlockface(player.yaw)
        val blocks = furnitureType.hitbox.getBlocks(center, direction, true)

        if (blocks.any { it.type != Material.AIR && it.type != Material.LIGHT }) {
            return
        }

        val id = manager.placedFurnitureHolder.createId()
        for (tempBlock in blocks) {
            tempBlock.type = Material.BARRIER
            tempBlock.setMetadata("furniture", manager.metadataManager.createMetadataValue(id.toString()))
        }

        this.playPlaceEffect(furnitureType, player, center)

        val location = center.location.clone().add(0.5, 0.5, 0.5)
        if (player.isSneaking && furnitureType.rotatable) {
            val standDirection = player.location.toVector().subtract(location.toVector())
            location.setDirection(standDirection)
        } else {
            location.setDirection(player.facing.oppositeFace.direction)
        }


        val placedFurniture = PlacedFurniture(
            manager,
            furnitureType.name,
            center,
            direction,
            location.yaw,
            id,
            item.clone().withAmount(1).toBase64()
        )

        item.amount--

        placedFurniture.sendCreationForWorld()
        placedFurniture.setLight(furnitureType)

        manager.placedFurnitureHolder.register(placedFurniture)
        player.setCooldown(item.type, 2)

    }

    private fun playPlaceEffect(
        furnitureType: FurnitureType,
        player: Player,
        center: Block
    ) {
        furnitureType.placeEffect?.let { effect ->
            effect.sound?.let { player.playSound(player, it, 1f, 1f) }
            effect.particle?.let {
                center.world.spawnParticle(it, center.location.clone().add(0.5, 0.5, 0.5), 10)
            }
        }
    }

}