package dev.devoirr.bitwigs.core.block.furniture.listener

import dev.devoirr.bitwigs.core.block.furniture.FurnitureManager
import dev.devoirr.bitwigs.core.toLocation
import dev.devoirr.bitwigs.core.toString
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDismountEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable

class FurnitureSitListener(private val manager: FurnitureManager) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onClick(event: PlayerInteractEvent) {

        if (!event.hasBlock())
            return

        if (event.action != Action.RIGHT_CLICK_BLOCK)
            return

        if (event.player.isSneaking)
            return

        val block = event.clickedBlock ?: return
        if (!block.hasMetadata("furniture"))
            return

        val furnitureId = block.getMetadata("furniture")[0].asInt()

        event.isCancelled = true

        val furniture =
            manager.placedFurnitureHolder.get(furnitureId) ?: return
        val furnitureType = manager.getFurnitureType(furniture.type) ?: return

        if (!furnitureType.allowSitting)
            return

        val armorStand = block.world.spawn(
            block.location.clone().add(0.5, furnitureType.sittingYOffset - 0.8, 0.5),
            ArmorStand::class.java
        )

        block.type = Material.AIR

        armorStand.isInvisible = true
        armorStand.isInvulnerable = true
        armorStand.setGravity(false)
        armorStand.setAI(false)

        armorStand.addPassenger(event.player)

        armorStand.setMetadata(
            "seat",
            manager.metadataManager.createMetadataValue(block.location.toString(block = true))
        )

        manager.getTaskManager().runTaskTimerForEntity(armorStand, object : BukkitRunnable() {
            override fun run() {
                armorStand.setRotation(event.player.yaw, event.player.pitch)
            }
        }, 1L)

        furniture.addSeat(armorStand)

    }

    @EventHandler
    fun onDismount(event: EntityDismountEvent) {
        if (event.dismounted.type != EntityType.ARMOR_STAND)
            return

        if (!event.dismounted.hasMetadata("seat"))
            return

        val block = event.dismounted.getMetadata("seat")[0].asString().toLocation().block
        block.type = Material.BARRIER

        event.dismounted.remove()
    }
}